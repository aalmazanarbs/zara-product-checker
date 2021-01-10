package com.gade.zaraproductcheckerapp.activities;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.gade.zaraproductcheckerapp.services.ZaraProductCheckerJobIntentServiceHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductcheckerapp.activities.viewmodels.ProductInfoViewModel;
import com.gade.zaraproductcheckerapp.adapters.ListProductInfoAdapter;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;
import com.gade.zaraproductcheckerapp.dialogs.NewProductAlertDialogBuilder;
import com.gade.zaraproductcheckerapp.util.NetUtil;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static com.gade.zaraproductcheckerapp.services.ZaraProductCheckerJobIntentService.PRODUCTS_INFO_REQUEST_RESULT_BROADCAST;
import static com.gade.zaraproductcheckerapp.services.ZaraProductCheckerJobIntentService.PRODUCTS_INFO_REQUEST_RESULT_DATA;
import static com.gade.zaraproductcheckerapp.util.NetUtil.isValidURL;
import static com.gade.zaraproductcheckerapp.util.RxUtil.applyCompletableSchedulers;
import static com.gade.zaraproductcheckerapp.util.UIUtil.animateViewSlideDown;
import static com.gade.zaraproductcheckerapp.util.UIUtil.animateViewToZero;
import static com.gade.zaraproductcheckerapp.util.UIUtil.showMessageSnackbar;
import static com.gade.zaraproductcheckerapp.util.UIUtil.showShortToast;
import static com.gade.zaraproductcheckerapp.util.notifications.ProductNotificationUtil.NOTIFICATIONS_EXTRA_INTENT;
import static com.gade.zaraproductcheckerapp.util.notifications.ProductNotificationUtil.NOTIFICATIONS_INTENT_CODE;
import static com.gade.zaraproductcheckerapp.util.notifications.ProductNotificationUtil.getNotificationManager;

public class MainActivity extends AppCompatActivity {

    private ProductInfoViewModel productInfoViewModel;
    private BroadcastReceiver productsInfoBroadcastReceiver;
    private NetUtil.NetworkStateReceiver networkStateReceiver;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout networkStateContainerRelativeLayout;
    private FloatingActionButton fabAddProduct;
    private RecyclerView.Adapter listProductAdapter;
    private AlertDialog newProductDialog;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productInfoViewModel = new ViewModelProvider(this).get(ProductInfoViewModel.class);

        // Load interface
        setContentView(R.layout.activity_main);
        initViews();
        setSupportActionBar(toolbar);
        setListeners();
        checkOpenedWithURL(getIntent());
        ZaraProductCheckerJobIntentServiceHandler.startOrStopPeriodicallyBackground(this.getApplication());
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver((productsInfoBroadcastReceiver), new IntentFilter(PRODUCTS_INFO_REQUEST_RESULT_BROADCAST));

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        MainActivity.this.registerReceiver(networkStateReceiver, intentFilter);

        loadOrRefreshProductList();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(productsInfoBroadcastReceiver);
        MainActivity.this.unregisterReceiver(networkStateReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        disposables.dispose();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkOpenedWithURL(intent);
        clearNotifications(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refresh_list) {
            loadOrRefreshProductList();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.navigation_drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        swipeRefreshLayout = findViewById(R.id.list_product_swipe_refresh_layout);
        networkStateContainerRelativeLayout = findViewById(R.id.network_state_container);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        listProductAdapter = new ListProductInfoAdapter(MainActivity.this);
    }

    private void setListeners() {
        // ProductsInfo changes
        productsInfoBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                swipeRefreshLayout.setRefreshing(true);
                final List<ProductInfo> productsInfo = (List) intent.getParcelableArrayListExtra(PRODUCTS_INFO_REQUEST_RESULT_DATA);

                if (productsInfo != null) {
                    ((ListProductInfoAdapter) listProductAdapter).refreshProductList(productsInfo);
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        };

        // Network changes
        networkStateReceiver = new NetUtil.NetworkStateReceiver();
        networkStateReceiver.setNetworkStateReceiverListener(isConnected -> {
            if (isConnected) {
                animateViewToZero(networkStateContainerRelativeLayout);
                animateViewToZero(swipeRefreshLayout);
            } else {
                animateViewSlideDown(MainActivity.this, swipeRefreshLayout, R.dimen.network_state_container_height);
                animateViewSlideDown(MainActivity.this, networkStateContainerRelativeLayout, R.dimen.network_state_container_height);
            }
        });

        // DrawerLayout
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.drawer_layout_open, R.string.drawer_layout_close);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // Navigation View
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_settings:
                    Intent preferencesIntent = new Intent(MainActivity.this, PreferencesActivity.class);
                    startActivity(preferencesIntent);
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });

        // Refresh List
        swipeRefreshLayout.setOnRefreshListener(this::loadOrRefreshProductList);

        // FloatingActionButton
        fabAddProduct.setOnClickListener(v -> showNewProductDialog(null));
    }

    private void checkOpenedWithURL(@NonNull Intent intent) {
        Uri zaraURL = intent.getData();
        if (zaraURL == null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            zaraURL = Uri.parse(intent.getStringExtra(Intent.EXTRA_TEXT));
        }

        if (zaraURL != null && isValidURL(zaraURL.toString())) {
            showNewProductDialog(zaraURL.toString());
        }
    }

    private void clearNotifications(@NonNull Intent intent) {
        if (intent.getExtras() == null) {
            return;
        }

        if (intent.getExtras().getInt(NOTIFICATIONS_EXTRA_INTENT) == NOTIFICATIONS_INTENT_CODE) {
            getNotificationManager(MainActivity.this).cancelAll();
        }
    }

    private void showNewProductDialog(final String URL) {
        if (newProductDialog != null && newProductDialog.isShowing()) {
            newProductDialog.dismiss();
        }

        final NewProductAlertDialogBuilder newProductAlertDialogBuilder = new NewProductAlertDialogBuilder(MainActivity.this, URL);
        newProductAlertDialogBuilder.setOnAddNewProductAlertDialogListener(productsInfo ->
            disposables.add(
                productInfoViewModel.addProductsInfo(productsInfo)
                    .compose(applyCompletableSchedulers())
                    .subscribe(() -> {
                        for(final ProductInfo productInfo: productsInfo) {
                            ((ListProductInfoAdapter) listProductAdapter).addProductToList(productInfo);
                        }

                        showMessageSnackbar(coordinatorLayout, MainActivity.this.getResources().getQuantityString(R.plurals.product_added, productsInfo.size()));
                    }, e -> {
                        if (productsInfo.size() == 1) {
                            showShortToast(MainActivity.this,
                                    MainActivity.this.getResources().getString(R.string.product_duplicated, productsInfo.get(0).getName(), productsInfo.get(0).getDesiredSize(), productsInfo.get(0).getDesiredColor()));
                        } else {
                            showShortToast(MainActivity.this, MainActivity.this.getResources().getString(R.string.products_duplicated));
                        }
                    })
            )
        );

        newProductDialog = newProductAlertDialogBuilder.show();
    }

    private void loadOrRefreshProductList() {
        swipeRefreshLayout.setRefreshing(true);
        ZaraProductCheckerJobIntentServiceHandler.startNowBackground(this.getApplication());
    }

    public void removedProductSnackbar(final ProductInfo productInfo, final int productPosition) {
        final boolean[] undoRemove = { false };

        Snackbar.make(coordinatorLayout,
                      getResources().getString(R.string.undo_product_info, productInfo.getName()),
                      Snackbar.LENGTH_LONG)
                .setAction(MainActivity.this.getString(R.string.undo), view -> {
                    undoRemove[0] = true;
                    ((ListProductInfoAdapter) listProductAdapter).reAddProductPendingToList(productInfo, productPosition);
                })
                .setActionTextColor(ContextCompat.getColor(MainActivity.this, R.color.soft_red))
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (!undoRemove[0]) {
                            disposables.add(
                                productInfoViewModel.removeProductInfo(productInfo)
                                    .compose(applyCompletableSchedulers())
                                    .subscribe()
                            );
                        }
                    }
                })
                .show();
    }
}
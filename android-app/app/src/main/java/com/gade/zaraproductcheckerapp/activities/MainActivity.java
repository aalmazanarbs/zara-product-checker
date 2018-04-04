package com.gade.zaraproductcheckerapp.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductcheckerapp.activities.viewmodels.ProductInfoViewModel;
import com.gade.zaraproductcheckerapp.adapters.ListProductInfoAdapter;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;
import com.gade.zaraproductcheckerapp.dialogs.NewProductAlertDialogBuilder;
import com.gade.zaraproductcheckerapp.handlers.ProductCheckerHandler;
import com.gade.zaraproductcheckerapp.services.ZaraProductCheckerService;
import com.gade.zaraproductcheckerapp.util.NetUtil;
import com.gade.zaraproductcheckerapp.util.RXUtil;
import com.gade.zaraproductcheckerapp.util.UIUtil;
import com.gade.zaraproductcheckerapp.util.notifications.ProductNotificationUtil;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

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
        productInfoViewModel = ViewModelProviders.of(this).get(ProductInfoViewModel.class);

        // Load interface
        setContentView(R.layout.activity_main);
        initViews();
        setSupportActionBar(toolbar);
        setListeners();
        checkOpenedWithURL(getIntent());
        ProductCheckerHandler.startStopCheckProductsService(this.getApplication());
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(MainActivity.this)
                .registerReceiver((productsInfoBroadcastReceiver), new IntentFilter(ProductCheckerHandler.PRODUCTS_INFO_BROADCAST));

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
    public boolean onOptionsItemSelected(MenuItem item) {
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
                List<ProductInfo> productsInfo = (List) intent.getParcelableArrayListExtra(ProductCheckerHandler.PRODUCTS_INFO_BROADCAST_LIST);

                if (productsInfo != null && productsInfo.size() > 0) {
                    ((ListProductInfoAdapter) listProductAdapter).refreshProductList(productsInfo);
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        };

        // Network changes
        networkStateReceiver = new NetUtil.NetworkStateReceiver();
        networkStateReceiver.setNetworkStateReceiverListener(isConnected -> {
            if (isConnected) {
                UIUtil.animateViewToZero(networkStateContainerRelativeLayout);
                UIUtil.animateViewToZero(swipeRefreshLayout);
            } else {
                UIUtil.animateViewSlideDown(MainActivity.this, swipeRefreshLayout, R.dimen.network_state_container_height);
                UIUtil.animateViewSlideDown(MainActivity.this, networkStateContainerRelativeLayout, R.dimen.network_state_container_height);
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

        if (zaraURL != null && NetUtil.isValidURL(zaraURL.toString())) {
            showNewProductDialog(zaraURL.toString());
        }
    }

    private void clearNotifications(@NonNull Intent intent) {
        if (intent.getExtras() == null) {
            return;
        }

        if (intent.getExtras().getInt(ProductNotificationUtil.NOTIFICATIONS_EXTRA_INTENT) == ProductNotificationUtil.NOTIFICATIONS_INTENT_CODE) {
            ProductNotificationUtil.getNotificationManager(MainActivity.this).cancelAll();
        }
    }

    private void showNewProductDialog(final String URL) {
        if (newProductDialog != null && newProductDialog.isShowing()) {
            newProductDialog.dismiss();
        }

        final NewProductAlertDialogBuilder newProductAlertDialogBuilder = new NewProductAlertDialogBuilder(MainActivity.this, URL);
        newProductAlertDialogBuilder.setOnAddNewProductAlertDialogListener(productsInfo ->
            disposables.add(
                productInfoViewModel.addProductstInfo(productsInfo)
                    .compose(RXUtil.applyCompletableSchedulers())
                    .subscribe(() -> {
                        for(final ProductInfo productInfo: productsInfo) {
                            ((ListProductInfoAdapter) listProductAdapter).addProductToList(productInfo);
                        }

                        UIUtil.showMessageSnackbar(coordinatorLayout, MainActivity.this.getResources().getQuantityString(R.plurals.product_added, productsInfo.size()));
                    }, e -> {
                        if (productsInfo.size() == 1) {
                            UIUtil.showShortToast(MainActivity.this,
                                    MainActivity.this.getResources().getString(R.string.product_duplicated, productsInfo.get(0).getName(), productsInfo.get(0).getDesiredSize(), productsInfo.get(0).getDesiredColor()));
                        } else {
                            UIUtil.showShortToast(MainActivity.this, MainActivity.this.getResources().getString(R.string.products_duplicated));
                        }
                    })
            )
        );

        newProductDialog = newProductAlertDialogBuilder.show();
    }

    private void loadOrRefreshProductList() {
        swipeRefreshLayout.setRefreshing(true);
        Intent zaraProductCheckerServiceIntent = new Intent(MainActivity.this, ZaraProductCheckerService.class);
        zaraProductCheckerServiceIntent.setAction(ProductCheckerHandler.PRODUCTS_INFO_BROADCAST_LIST_ALWAYS);
        MainActivity.this.startService(zaraProductCheckerServiceIntent);
    }

    public void removedProductSnackbar(final ProductInfo productInfo, final int productPosition) {
        final boolean[] undoRemove = {false};

        Snackbar.make(coordinatorLayout,
                      getResources().getString(R.string.undo_product_info, productInfo.getName()),
                      Snackbar.LENGTH_LONG)
                .setAction(MainActivity.this.getString(R.string.undo), view -> {
                    undoRemove[0] = true;
                    ((ListProductInfoAdapter) listProductAdapter).reAddProductPendingToList(productInfo, productPosition);
                }).addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (!undoRemove[0]) {
                            disposables.add(
                                productInfoViewModel.removeProductInfo(productInfo)
                                    .compose(RXUtil.applyCompletableSchedulers())
                                    .subscribe()
                            );
                        }
                    }
                }).show();
    }
}
package com.gade.zaraproductcheckerapp.adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductchecker.UIFormatter;
import com.gade.zaraproductcheckerapp.activities.MainActivity;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

import java.util.ArrayList;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static com.gade.zaraproductcheckerapp.util.NetUtil.shouldOpenURLInChromeCustomTabs;
import static com.gade.zaraproductcheckerapp.util.UIUtil.DEFAULT_IMAGE_REQUEST_OPTIONS;
import static com.gade.zaraproductcheckerapp.util.UIUtil.showShortToast;

public class ListProductInfoAdapter extends RecyclerView.Adapter<ListProductInfoAdapter.ListProductInfoViewHolder> {

    private List<ProductInfo> productsInfo;
    private final Activity mainActivity;
    private RecyclerView recyclerView;

    public ListProductInfoAdapter(final Activity activity) {
        this.mainActivity = activity;
        this.productsInfo = new ArrayList<>();

        configureRecyclerView();
    }

    static class ListProductInfoViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout container;
        final CardView cardView;
        final ImageView productImage;
        final TextView productName;
        final TextView productSize;
        final TextView productSizeInfo;
        final TextView productPriceInfo;
        final ImageView productNotFound;
        final ImageView productAvailabilityChanges;
        final ImageView productPriceChanges;
        final ImageView productMoreInfo;

        ListProductInfoViewHolder(View itemView) {
            super(itemView);
            this.container = itemView.findViewById(R.id.item_layout_container);
            this.cardView = itemView.findViewById(R.id.card_view);
            this.productImage = itemView.findViewById(R.id.product_image);
            this.productName = itemView.findViewById(R.id.product_name);
            this.productSize = itemView.findViewById(R.id.product_size);
            this.productSizeInfo = itemView.findViewById(R.id.product_size_info);
            this.productPriceInfo = itemView.findViewById(R.id.product_price_info);
            this.productNotFound = itemView.findViewById(R.id.product_not_found);
            this.productAvailabilityChanges = itemView.findViewById(R.id.product_availability_changes);
            this.productPriceChanges = itemView.findViewById(R.id.product_price_changes);
            this.productMoreInfo = itemView.findViewById(R.id.product_more_info);
        }
    }

    @NonNull
    @Override
    public ListProductInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_layout, parent, false);
        return new ListProductInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListProductInfoViewHolder listProductViewHolder, final int listPosition) {
        final CardView cardView = listProductViewHolder.cardView;
        final ImageView productImage = listProductViewHolder.productImage;
        final TextView productName = listProductViewHolder.productName;
        final TextView productSize = listProductViewHolder.productSize;
        final TextView productSizeInfo = listProductViewHolder.productSizeInfo;
        final TextView productPriceInfo = listProductViewHolder.productPriceInfo;
        final ImageView productNotFound = listProductViewHolder.productNotFound;
        final ImageView productAvailabilityChanges = listProductViewHolder.productAvailabilityChanges;
        final ImageView productPriceChanges = listProductViewHolder.productPriceChanges;
        final ImageView productMoreInfo = listProductViewHolder.productMoreInfo;

        final ProductInfo productInfo = productsInfo.get(listPosition);

        Glide.with(productImage.getContext())
             .load(productInfo.getImageUrl())
             .apply(DEFAULT_IMAGE_REQUEST_OPTIONS)
             .into(productImage);

        productName.setText(productInfo.getName());
        if (productInfo.getDesiredSize().length() > 2) {
            productSize.setTextSize(COMPLEX_UNIT_SP, 20);
        }
        productSize.setText(productInfo.getDesiredSize());
        productSizeInfo.setText(mainActivity.getResources().getString(R.string.color_and_availability,
                                                                      productInfo.getDesiredColor(),
                                                                      UIFormatter.productAvailability(productInfo.getAvailability())));
        productPriceInfo.setText(UIFormatter.productPrice(productInfo.getPrice()));

        if (productInfo.isNotFound()) {
            productNotFound.setVisibility(View.VISIBLE);
        } else {
            productNotFound.setVisibility(View.GONE);
        }

        if (productInfo.hasSizeStatusChanged()) {
            productAvailabilityChanges.setVisibility(View.VISIBLE);
        } else {
            productAvailabilityChanges.setVisibility(View.GONE);
        }

        if (productInfo.hasPriceChanged()) {
            productPriceChanges.setVisibility(View.VISIBLE);
        } else {
            productPriceChanges.setVisibility(View.GONE);
        }

        if (!productInfo.getUrl().isEmpty()) {
            cardView.setOnClickListener(view -> shouldOpenURLInChromeCustomTabs(mainActivity, productInfo.getUrl()));
        }

        productMoreInfo.setOnClickListener(view -> showMoreInfoPopUp(productInfo));

        productNotFound.setOnClickListener(view -> showShortToast(mainActivity, mainActivity.getResources().getString(R.string.product_not_found_info)));

        setAnimation(listProductViewHolder.container);
    }

    @Override
    public int getItemCount() {
        return productsInfo.size();
    }

    private void configureRecyclerView () {
        recyclerView = mainActivity.findViewById(R.id.products_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(this);
        recyclerView.setHasFixedSize(true);

        // Recycler Listener
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int positionRemoved = viewHolder.getBindingAdapterPosition();
                ((MainActivity) mainActivity).removedProductSnackbar(initRemoveProductFromList(positionRemoved), positionRemoved);
            }

            @Override
            public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setAnimation(final View viewToAnimate) {
        final Animation animation = AnimationUtils.loadAnimation(mainActivity, android.R.anim.slide_in_left);
        animation.setDuration(300);
        viewToAnimate.startAnimation(animation);
    }

    public void addProductToList(ProductInfo productInfo) {
        final int positionAdd = productsInfo.size();
        productsInfo.add(positionAdd, productInfo);
        this.notifyItemInserted(positionAdd);
        recyclerView.smoothScrollToPosition(positionAdd);
    }

    public void reAddProductPendingToList(@NonNull final ProductInfo productInfo, final int position) {
        productsInfo.add(position, productInfo);
        this.notifyItemInserted(position);
    }

    private ProductInfo initRemoveProductFromList(final int position) {
        final ProductInfo productPendingRemoval = productsInfo.get(position);
        productsInfo.remove(position);
        this.notifyItemRemoved(position);
        return productPendingRemoval;
    }

    public void refreshProductList(@NonNull List<ProductInfo> productsInfo) {
        this.productsInfo.clear();
        this.productsInfo.addAll(productsInfo);
        this.productsInfo = productsInfo;
        this.notifyDataSetChanged();
    }

    private void showMoreInfoPopUp(final ProductInfo productInfo) {
        final AlertDialog.Builder moreInfoAlertDialog = new AlertDialog.Builder(mainActivity, R.style.AppCompatAlertDialogStyleProductInfo);
        final View moreInfoAlertDialogView = View.inflate(mainActivity, R.layout.more_info_product_popup, null);
        moreInfoAlertDialog.setView(moreInfoAlertDialogView);
        moreInfoAlertDialog.setTitle(productInfo.getName());
        moreInfoAlertDialog.setIcon(R.drawable.ic_info_outline_black_18dp);
        moreInfoAlertDialog.setPositiveButton("Ok", null);

        final TextView sizeInfoLabel = moreInfoAlertDialogView.findViewById(R.id.product_info_size_label);
        sizeInfoLabel.setText(mainActivity.getResources().getString(R.string.size_title,
                                                                    sizeInfoLabel.getText().toString(),
                                                                    productInfo.getDesiredSize()));

        ((TextView) moreInfoAlertDialogView.findViewById(R.id.product_info_size)).setText(mainActivity.getResources().getString(R.string.color_and_availability,
                                                                                                                                productInfo.getDesiredColor(),
                                                                                                                                UIFormatter.productAvailability(productInfo.getAvailability())));
        ((TextView) moreInfoAlertDialogView.findViewById(R.id.product_info_price)).setText(UIFormatter.productPrice(productInfo.getPrice()));

        moreInfoAlertDialog.create().show();
    }
}

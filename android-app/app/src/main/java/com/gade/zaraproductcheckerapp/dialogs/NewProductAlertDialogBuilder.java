package com.gade.zaraproductcheckerapp.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.gade.zaraproductchecker.ProductApi;
import com.gade.zaraproductchecker.ProductJsonHelper;
import com.gade.zaraproductchecker.model.ProductData;
import com.gade.zaraproductchecker.model.ProductStatus;
import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;
import com.gade.zaraproductcheckerapp.dialogs.model.NewProductDialogState;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

import static com.gade.zaraproductchecker.ApiHelper.searchProductStatus;
import static com.gade.zaraproductchecker.util.OptionalUtil.ifPresentOrElse;
import static com.gade.zaraproductcheckerapp.util.RxUtil.applySingleSchedulers;
import static com.gade.zaraproductcheckerapp.util.UIUtil.showShortToast;
import static java.util.Collections.singletonList;

public class NewProductAlertDialogBuilder extends AlertDialog.Builder {

    private AlertDialog newProductAlertDialog;
    private EditText newProductURLEditText;
    private Spinner newProductSizeSpinner;
    private Spinner newProductColorSpinner;
    private ProgressBar newProductProgressBar;
    private final Activity activity;
    private NewProductDialogState newProductDialogState;
    private final CompositeDisposable disposables = new CompositeDisposable();

    // Interface for Activities
    private OnAddNewProductAlertDialogListener onAddNewProductAlertDialogListener;

    private static class ZaraErrorException extends RuntimeException {
        ZaraErrorException(String message) {
            super(message);
        }
    }

    public NewProductAlertDialogBuilder(@NonNull final Activity activity, @Nullable final String incomingURL) {
        super(activity, R.style.AppCompatAlertDialogStyleNewProduct);
        this.activity = activity;
        this.newProductDialogState = new NewProductDialogState(incomingURL);
    }

    public void setOnAddNewProductAlertDialogListener(@NonNull OnAddNewProductAlertDialogListener onAddNewProductAlertDialogListener) {
        this.onAddNewProductAlertDialogListener = onAddNewProductAlertDialogListener;
    }

    public interface OnAddNewProductAlertDialogListener {
        void addNewProductAlertDialog(final List<ProductInfo> newProductsInfo);
    }

    @Override
    public AlertDialog show() {
        newProductAlertDialog = super.create();
        newProductAlertDialog.setCancelable(false);
        newProductAlertDialog.setCanceledOnTouchOutside(false);
        newProductAlertDialog.setView(initViews());
        if (newProductDialogState.getIncomingZaraURL() != null && !newProductDialogState.getIncomingZaraURL().isEmpty()) {
            newProductURLEditText.setText(newProductDialogState.getIncomingZaraURL());
        }
        newProductAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getString(R.string.dialog_search), emptyOnClickListener);
        newProductAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getContext().getString(R.string.dialog_reset), emptyOnClickListener);
        newProductAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getContext().getString(R.string.dialog_cancel), emptyOnClickListener);
        newProductAlertDialog.show();
        newProductAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(searchAddNewProductListener);
        newProductAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(cancelAddNewProductListener);
        newProductAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(resetAddNewProductListener);
        return newProductAlertDialog;
    }

    private View initViews () {
        View newProductView = View.inflate(getContext(), R.layout.new_product_dialog, null);
        newProductURLEditText = newProductView.findViewById(R.id.product_url);
        newProductSizeSpinner = newProductView.findViewById(R.id.product_size);
        newProductColorSpinner = newProductView.findViewById(R.id.product_color);
        newProductProgressBar = newProductView.findViewById(R.id.new_product_progressbar);
        return newProductView;
    }

    private final DialogInterface.OnClickListener emptyOnClickListener = (dialog, which) -> {};

    private final View.OnClickListener searchAddNewProductListener = view ->
        disposables.add(
            processDialog()
                    .compose(applySingleSchedulers())
                    .doOnSubscribe(__ -> {
                        hideKeyBoard();
                        newProductProgressBar.setVisibility(View.VISIBLE);
                    })
                    .doFinally(() -> newProductProgressBar.setVisibility(View.GONE))
                    .subscribe(productsInfo -> {
                        if (productsInfo.size() > 0 && onAddNewProductAlertDialogListener != null) {
                            onAddNewProductAlertDialogListener.addNewProductAlertDialog(productsInfo);
                            newProductAlertDialog.dismiss();
                        }
                    }, exception -> {
                        if (exception instanceof ZaraErrorException) {
                            showShortToast(getContext(), exception.getMessage());
                        } else {
                            exception.printStackTrace();
                            showShortToast(getContext(), getContext().getString(R.string.dialog_unknown_error));
                        }
                    })
    );

    private final View.OnClickListener cancelAddNewProductListener = view -> closeDialog();

    private final View.OnClickListener resetAddNewProductListener = view -> {
        newProductURLEditText.getText().clear();

        if (newProductDialogState.getIncomingZaraURL() != null && !newProductDialogState.getIncomingZaraURL().isEmpty()) {
            newProductURLEditText.setText(newProductDialogState.getIncomingZaraURL());
        }

        if (newProductDialogState.isDoneGetZaraDataPhase()) {
            newProductURLEditText.setEnabled(true);
            newProductSizeSpinner.setVisibility(View.GONE);
            newProductSizeSpinner.setAdapter(null);
            newProductColorSpinner.setVisibility(View.GONE);
            newProductColorSpinner.setAdapter(null);
            newProductAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(getContext().getString(R.string.dialog_search));
            newProductDialogState.setSizes(null);
            newProductDialogState.setColors(null);
            newProductDialogState.setDoneGetZaraDataPhase(false);
            newProductDialogState.setZaraJSONResponse(null);
        }
    };

    private void closeDialog() {
        disposables.clear();
        newProductAlertDialog.dismiss();
    }

    private void hideKeyBoard() {
        final InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && newProductAlertDialog.getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(newProductAlertDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private Single<List<ProductInfo>> processDialog() {
        final String productURL = newProductURLEditText.getText().toString();
        return Single.fromCallable(() -> {
            if (!newProductDialogState.isDoneGetZaraDataPhase()) {
                getZaraDataPhase(productURL);
                return new ArrayList<>(); // Single can not return null...
            } else {
                return constructProductsInfo(productURL);
            }
        });
    }

    private void getZaraDataPhase(final String productURL) throws ZaraErrorException {
        ifPresentOrElse(
                ProductApi.doCall(productURL),
                zaraJSONResponse -> {
                    final List<String> sizes = ProductJsonHelper.getSizesFromJSONString(zaraJSONResponse);
                    final List<String> colors = ProductJsonHelper.getColorsFromJSONString(zaraJSONResponse);
                    if (sizes.isEmpty() || colors.isEmpty()) {
                        throw new ZaraErrorException(getContext().getString(R.string.dialog_zara_response_error));
                    }
                    newProductDialogState.setSizes(cloneList(sizes));
                    newProductDialogState.setColors(cloneList(colors));

                    if (sizes.size() > 1) {
                        sizes.add(getContext().getString(R.string.dialog_all_sizes));
                    }
                    if (colors.size() > 1) {
                        colors.add(getContext().getString(R.string.dialog_all_colors));
                    }

                    activity.runOnUiThread(() -> {
                        final ArrayAdapter<String> colorsArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, colors);
                        colorsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        newProductColorSpinner.setAdapter(colorsArrayAdapter);
                        newProductColorSpinner.setVisibility(View.VISIBLE);

                        final ArrayAdapter<String> sizesArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, sizes);
                        sizesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        newProductSizeSpinner.setAdapter(sizesArrayAdapter);
                        newProductSizeSpinner.setVisibility(View.VISIBLE);

                        newProductAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(getContext().getString(R.string.dialog_add));

                        newProductURLEditText.setEnabled(false);
                    });

                    newProductDialogState.setZaraJSONResponse(zaraJSONResponse);
                    newProductDialogState.setDoneGetZaraDataPhase(true);
                },
                () -> { throw new ZaraErrorException(getContext().getString(R.string.dialog_url_error)); }
        );
    }

    private List<ProductInfo> constructProductsInfo(final String productURL) throws ZaraErrorException {
        return ProductJsonHelper.getProductDataFromJSONString(newProductDialogState.getZaraJSONResponse())
                .map(productData -> createProductsInfoFromUserEntries(productData, productURL))
                .orElseThrow(() -> new ZaraErrorException(getContext().getString(R.string.dialog_product_error)));
    }

    private List<ProductInfo> createProductsInfoFromUserEntries(final ProductData productData, final String productURL) throws ZaraErrorException {
        final List<ProductStatus> productStatuses = ProductJsonHelper.getProductStatuses(newProductDialogState.getZaraJSONResponse());

        final String desiredSize = newProductSizeSpinner.getSelectedItem().toString();
        final String desiredColor = newProductColorSpinner.getSelectedItem().toString();
        final List<String> desiredSizes = desiredSize.equals(getContext().getString(R.string.dialog_all_sizes)) ? newProductDialogState.getSizes() : singletonList(desiredSize);
        final List<String> desiredColors = desiredColor.equals(getContext().getString(R.string.dialog_all_colors)) ? newProductDialogState.getColors() : singletonList(desiredColor);
        final List<ProductInfo> productsInfo = new ArrayList<>();

        for (final String size: desiredSizes) {
            for (final String color: desiredColors) {
                productsInfo.add(createProductInfo(productData, productStatuses, productURL, size, color));
            }
        }

        return productsInfo;
    }

    private ProductInfo createProductInfo(final ProductData productData, final List<ProductStatus> productStatuses, final String productURL, final String desiredSize, final String desiredColor) throws ZaraErrorException {
        return searchProductStatus(productStatuses, productData.getApiId(), desiredSize, desiredColor)
                .map(productStatus -> {
                    final ProductInfo productInfo = new ProductInfo();
                    productInfo.setApiId(productData.getApiId());
                    productInfo.setName(productData.getName());
                    productInfo.setUrl(productURL);
                    productInfo.setImageUrl(productData.findUrlByColor(desiredColor));
                    productInfo.setDesiredSize(desiredSize);
                    productInfo.setDesiredColor(desiredColor);
                    productInfo.setAvailability(productStatus.getAvailability());
                    productInfo.setPrice(productStatus.getPrice());
                    return productInfo;
                })
                .orElseThrow(() -> new ZaraErrorException(getContext().getString(R.string.dialog_product_error)));
    }

    private <T> List<T> cloneList(final List<T> list) {
        List<T> clone = new ArrayList<>(list.size());
        clone.addAll(list);
        return clone;
    }
}

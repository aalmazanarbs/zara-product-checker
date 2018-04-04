package com.gade.zaraproductcheckerapp.dialogs.model;

import java.util.List;

public class NewProductDialogState {

    private final String incomingZaraURL;
    private boolean doneGetZaraDataPhase;
    private String zaraJSONResponse;
    private List<String> colors;
    private List<String> sizes;

    public NewProductDialogState(final String incomingZaraURL) {
        this.incomingZaraURL = incomingZaraURL;
    }

    public String getIncomingZaraURL() {
        return incomingZaraURL;
    }

    public boolean isDoneGetZaraDataPhase() {
        return doneGetZaraDataPhase;
    }

    public void setDoneGetZaraDataPhase(boolean doneGetZaraDataPhase) {
        this.doneGetZaraDataPhase = doneGetZaraDataPhase;
    }

    public String getZaraJSONResponse() {
        return zaraJSONResponse;
    }

    public void setZaraJSONResponse(String zaraJSONResponse) {
        this.zaraJSONResponse = zaraJSONResponse;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(final List<String> colors) {
        this.colors = colors;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(final List<String> sizes) {
        this.sizes = sizes;
    }
}

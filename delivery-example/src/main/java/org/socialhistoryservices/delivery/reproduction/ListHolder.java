package org.socialhistoryservices.delivery.reproduction;

import java.util.List;

public class ListHolder<T> {
    private List<T> items;

    public ListHolder() {
    }

    public ListHolder(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }
}

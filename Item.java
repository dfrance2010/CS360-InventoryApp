package com.zybooks.inventoryapp;

import java.util.Objects;

public class Item {
    private int id;
    private String item;
    private String description;
    private String quantity;

    public Item() {}

    public Item(int id, String name, String description, String quantity) {
        this.id = id;
        item = name;
        this.description = description;
        this.quantity = quantity;
    }

    public Item(String name, String description, String quantity) {
        this(-1, name, description, quantity);
    }

    public Item(int id, Item item) {
        this(id, item.item, item.quantity, item.quantity);
    }

    public Item(Item i) {
        this(i.id, i.item, i.description, i.quantity);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return item;
    }

    public void setName(String name) {
        this.item = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id && Objects.equals(this.item, item.item) && Objects.equals(description, item.description) && Objects.equals(quantity, item.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, item, description, quantity);
    }

}

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

    /**
     * Get id of item
     * @return - item id
     */
    public int getId() {
        return id;
    }

    /**
     * Get name of item
     * @return - name of item
     */
    public String getName() {
        return item;
    }

    /**
     * Set name of item
     * @param name - name of item
     */
    public void setName(String name) {
        this.item = name;
    }

    /**
     * Get description of item
     * @return - description of item
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get quantity of item
     * @return - quantity of item
     */
    public String getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id && Objects.equals(this.item, item.item) && Objects.equals(description,
                item.description) && Objects.equals(quantity, item.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, item, description, quantity);
    }

}

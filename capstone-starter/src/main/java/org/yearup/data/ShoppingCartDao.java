package org.yearup.data;

import org.yearup.models.ShoppingCartItem;

import java.util.List;

public interface ShoppingCartDao
{
    List<ShoppingCartItem> getCartItems(int userId);
    void addProduct(int userId, int productId);
    void updateQuantity(int userId, int productId, int quantity);
    void clearCart(int userId);
    void addToCart(int userId, int productId);
}


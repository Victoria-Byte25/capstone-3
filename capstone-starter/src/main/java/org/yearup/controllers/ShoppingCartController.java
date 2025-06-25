package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.util.List;
import java.security.Principal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController
{
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao)
    {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            List<ShoppingCartItem> cartItems = shoppingCartDao.getCartItems(userId);

            ShoppingCart cart = new ShoppingCart();
            for (ShoppingCartItem item : cartItems)
            {
                int productId = item.getProduct().getProductId();
                cart.getItems().put(productId, item);
            }

            cart.getTotal();
            return cart;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to load cart.");
        }
    }

    @PostMapping("/products/{productId}")
    public void addToCart(@PathVariable int productId, Principal principal) {
        try {
            System.out.println("STEP 1 - Principal received: " + principal);

            String userName = principal.getName();
            System.out.println("STEP 2 - Username from principal: " + userName);

            User user = userDao.getByUserName(userName);
            System.out.println("STEP 3 - User from DB: " + user);

            int userId = user.getId();
            System.out.println("STEP 4 - User ID: " + userId);

            shoppingCartDao.addToCart(userId, productId);
            System.out.println("STEP 5 - Cart updated!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to add product to cart.");
        }
    }

}

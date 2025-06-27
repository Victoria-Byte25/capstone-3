EasyShop - Spring Boot E-Commerce API
EasyShop is a beginner-friendly, full-stack e-commerce backend project built using Spring Boot, Java, and MySQL. 

Features
1.User registration & login with JWT
2.Role-based access (@PreAuthorize)
3.Secure profile updates
4.Add/Edit/Delete products
5.Filter/search products by name or category
6.Category management
7.Shopping cart logic (in progress)
8.Clean layered structure using DAO + Controller

What I'm Proud Of
Product Filtering (Search)
.I implemented a dynamic search endpoint that lets users filter products by name or category using Java Streams.
.I implemented custom quantity controls using endpoints, allowing users to increase or decrease the quantity of item in their cart.


Controller:
@GetMapping("/search")
public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
List<Product> result = productDao.searchByNameOrCategory(query);
return ResponseEntity.ok(result);
}

DAO:
public List<Product> searchByNameOrCategory(String query) {
return productList.stream()
.filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()) ||
p.getCategory().toLowerCase().contains(query.toLowerCase()))
.collect(Collectors.toList());
}

This update shows:
.Practical use of Java Streams and .filter()
.Clean code and separation of logic
.Real-world application: Users need to search and find products easily
.My understanding of how to connect controller and DAO layers


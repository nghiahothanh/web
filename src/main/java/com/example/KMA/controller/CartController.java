package com.example.KMA.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.KMA.mapper.ProductMapper;
import com.example.KMA.model.PriceUpdateResponse;
import com.example.KMA.model.Product;
import com.example.KMA.model.cart;
import com.example.KMA.model.cartItem;
import com.example.KMA.model.cartManager;
import com.example.KMA.service.ProductService;
import com.mysql.cj.Session;

@Controller
@RequestMapping("/cart")
@CrossOrigin(origins = "http://localhost:8080")
public class CartController {

	@Autowired
	cartManager CartManager;
	@Autowired 
	ProductMapper productMapper;
	@Autowired
	ProductService service;
	@RequestMapping("/add")
	public String add(Model model,
			HttpSession session,
			 @RequestParam("id") int id,
			@RequestParam(value = "qty" , required = false , defaultValue ="1" ) int qty)
	{
		
		Product product = service.get(id);
		cart Cart= CartManager.getCart(session);
		Cart.addItem(product, qty);
		model.addAttribute("cart", Cart);
		return "cart.html";
		
	}
	@RequestMapping(value = "/update", method = RequestMethod.POST)


    public ResponseEntity<cartItem> updatePrice(Model model,HttpSession session,@RequestParam("price") int newPrice,@RequestParam("index") int id,@RequestParam("quantity") int quantity) {
        // Xử lý logic ở đây, ví dụ như lưu giá trị vào CSDL
    	Product product = service.get(id+1);
    	cart Cart  = CartManager.getCart(session);
    	   Cart.updateItem(product, quantity,newPrice);
		   cartItem CartItem = new cartItem(product);
		   CartItem.setQuantity(quantity);
		  CartItem.setSubTotal(newPrice);
		  System.out.println(Cart.getTotal());
		    return new ResponseEntity<>(CartItem, HttpStatus.OK);
    }

	    @RequestMapping("/remove")
	    public String remove(HttpSession session, 
	    		Model model,@RequestParam("id") int id){
	    	Product product = service.get(id);
	    	cart Cart = CartManager.getCart(session);
	        Cart.removeItem(product);
	        model.addAttribute("cart", Cart);
	        return "cart";
	    }
	    @RequestMapping("/listcart")
	    public String liSt(HttpSession session, Model model) {
	    	cart Cart = CartManager.getCart(session);
	    	model.addAttribute("cart", Cart);
	    	return "cart.html";
	    }
	  
	    @RequestMapping("/insert")
	    public String insert(Model model) {
	    	Product product = new Product();
	    	model.addAttribute("product", product);
	    	
	    	return "add-product";
	    	
	    }
	    @PostMapping("/save-product")
	    public String saveProduct( Product product , @RequestParam("imageFile") MultipartFile imageFile,BindingResult result) throws IOException {
	    	  if (result.hasErrors()) {
	    	        return "product-form";
	    	    }
	    	service.save(product,imageFile);
	        return "redirect:/";
	    }
	    @PostMapping("/checkout")
	    public String checkout(@RequestParam(value = "productIds" ) List<String> productIdStrings,
	    		@RequestParam(value = "qty") List<Integer> ListQty) {
	        // Convert productIdStrings to List<Integer> using Integer.parseInt()
	
	        List<Integer> productIds = new ArrayList<>();
	        List<Integer> IntQty = new ArrayList<>();
	        for (String productIdString : productIdStrings) {
	            productIds.add(Integer.parseInt(productIdString));
	        }
	        cart Cart = new cart();
	    	 
	    	
	       for (Integer integer : productIds) {
			Product product = service.get(integer);	
			int quantity= product.getPrice();
			
			Cart.addItem(product,quantity);
			
		}
	        int total = (int) Cart.getTotal();
	        
	        return "index";
	    }

	    
}

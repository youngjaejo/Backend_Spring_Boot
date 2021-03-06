package com.dev.backend.api;

import com.dev.backend.model.Book;
import com.dev.backend.repository.BookRepository;
import com.dev.backend.repository.UserRepository;
import com.dev.backend.model.User;
import com.dev.backend.service.BookServiceImp;
import com.dev.backend.service.UserServiceImp;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

//user web page 
@Controller
@RequestMapping("/webHome")
public class WebPageController {

  @Autowired
    private BookRepository bookRepository;
  @Autowired
    private BookServiceImp bookService;
  @Autowired
    private UserRepository user;
   
    
   
    
    @RequestMapping("/webMain" )
    public String viewWebPage(Map<String, Object> model)
    {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      User searchedUser=user.findByEmail2(auth.getName());
 
     List<String> listImg=(List<String>) bookRepository.getAllImg();
     List<String> listTitle=(List<String>) bookRepository.getAllTitle();
     
     if(searchedUser!=null)
     model.put("user_name", searchedUser.getFirstName());
     
     model.put("listImg", listImg);
     model.put("listTitle", listTitle);
    
     return "Web_main";
    }
    
  @RequestMapping("/webUserRegester" )
    public String showNewUserFromWeb(Model model){
      User customer=new User();
      model.addAttribute("user", customer);
      return "User_Regester";
    }
 
  // public ModelAndView search(@RequestParam String keyword){
  //   ModelAndView mav= new ModelAndView("book_search");
  //   List<Book> result=bookRepository.searchByBook(keyword);
  //   mav.addObject("result", result);
  //   return mav;
  // }
  
    @RequestMapping("{img_name}")
    public String newPage(@PathVariable(name="img_name") String img_name, Map<String, Object> model ){
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      User searchedUser=user.findByEmail2(auth.getName());
      
      Book result=bookRepository.searchByImage(img_name);
      model.put("listBook", result);
      if(searchedUser!=null)
      model.put("user_name", searchedUser.getFirstName());
      return "Web_bookPage";
    }
    @RequestMapping("/cart/{book_id}")
    public String passBookToCart(@PathVariable(name="book_id") String book_id, Map<String, Object> model){
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      User searchedUser=user.findByEmail2(auth.getName());

      Optional<Book> result=bookRepository.findById(Integer.parseInt(book_id));

      bookService.saveAtCart(searchedUser,result.get());
      List<Book> booksInCart=bookRepository.getAllBookInCart(searchedUser.getId());
      model.put("listBook",booksInCart);
      if(searchedUser!=null)
      model.put("user_name", searchedUser.getFirstName());
      return "Web_customer_cart";
    }
    @RequestMapping("/cart")
    public String bookCart(Map<String, Object> model){

      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User searchedUser=user.findByEmail2(auth.getName());

      List<Book> booksInCart=bookRepository.getAllBookInCart(searchedUser.getId());
      model.put("listBook",booksInCart);
      
      if(searchedUser!=null)
      model.put("user_name", searchedUser.getFirstName());

      return "Web_customer_cart";
    }

    // @RequestMapping("/cart/{user_id}")
    // public ModelAndView showNewUserFrom(@PathVariable(name="user_id") String user_id){
    //   ModelAndView mav=new ModelAndView("customer_cart");
    //   Optional<Book> result=bookRepository.findById(Integer.parseInt(user_id));
    //   System.out.println(result.get());
    //   mav.addObject("listBook", result.get());
  
    //   return mav;
    // }
  
     
    @RequestMapping("/toWebMain")
    public String toMain(){
    
      return "redirect:/webHome/webMain";
  }
  
  @RequestMapping("/searchByBookW")
  public ModelAndView searchByBook(@RequestParam String keyword){
    ModelAndView mav= new ModelAndView("Web_Book_Searching");
    List<String> listImg=(List<String>) bookRepository.searchImgByBookW(keyword);
    List<String> listTitle=(List<String>) bookRepository.searchTitleByBookW(keyword);
    
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User searchedUser=user.findByEmail2(auth.getName());
    if(searchedUser!=null)
    mav.addObject("user_name", searchedUser.getFirstName()); 

    mav.addObject("listImg", listImg);
    mav.addObject("listTitle", listTitle);
    return mav;
  }
  @RequestMapping("/searchByIsbnW")
  public ModelAndView searchByIsbn(@RequestParam String keyword){
    ModelAndView mav= new ModelAndView("Web_Book_Searching");
    List<String> listImg=(List<String>) bookRepository.searchImgByIsbnW(keyword);
    List<String> listTitle=(List<String>) bookRepository.searchTitleByIsbnW(keyword);
    
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User searchedUser=user.findByEmail2(auth.getName());
    if(searchedUser!=null)
    mav.addObject("user_name", searchedUser.getFirstName()); 

    mav.addObject("listImg", listImg);
    mav.addObject("listTitle", listTitle);
    return mav;
  }
  @RequestMapping("/searchByTitleW")
  public ModelAndView searchByTitle(@RequestParam String keyword){
    ModelAndView mav= new ModelAndView("Web_Book_Searching");
    List<String> listImg=(List<String>) bookRepository.searchImgByTitleW(keyword);
    List<String> listTitle=(List<String>) bookRepository.searchImgTitleByTitleW(keyword);
    

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User searchedUser=user.findByEmail2(auth.getName());
    if(searchedUser!=null)
    mav.addObject("user_name", searchedUser.getFirstName()); 

    mav.addObject("listImg", listImg);
    mav.addObject("listTitle", listTitle);
    return mav;
  }
  @RequestMapping("/searchByCategoryW")
  public ModelAndView searchByCategory(@RequestParam String keyword){
    ModelAndView mav= new ModelAndView("Web_Book_Searching");
    List<String> listImg=(List<String>) bookRepository.searchImgByCategoryW(keyword);
    List<String> listTitle=(List<String>) bookRepository.searchImgTitleByCategoryW(keyword);
    
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User searchedUser=user.findByEmail2(auth.getName());
    if(searchedUser!=null)
    mav.addObject("user_name", searchedUser.getFirstName()); 


    mav.addObject("listImg", listImg);
    mav.addObject("listTitle", listTitle);
    return mav;
  }
  @RequestMapping("/searchByAuthorW")
  public ModelAndView searchByAuthor(@RequestParam String keyword){
    ModelAndView mav= new ModelAndView("Web_Book_Searching");
    List<String> listImg=(List<String>) bookRepository.searchImgByAuthorW(keyword);
    List<String> listTitle=(List<String>) bookRepository.searchImgTitleByAuthorW(keyword);
    

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User searchedUser=user.findByEmail2(auth.getName());
    if(searchedUser!=null)
    mav.addObject("user_name", searchedUser.getFirstName()); 


    mav.addObject("listImg", listImg);
    mav.addObject("listTitle", listTitle);
    return mav;
  }
    
}
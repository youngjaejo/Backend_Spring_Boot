package com.dev.backend.api;

import com.dev.backend.model.User;
import com.dev.backend.payload.request.VertificationNumber;
import com.dev.backend.service.UserService;
import com.dev.backend.repository.AddressRepository;
import com.dev.backend.repository.RoleRepository;
import com.dev.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.dev.backend.model.Address;
import com.dev.backend.model.Role;
import javax.validation.Valid;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/user")
public class UserController {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private JavaMailSender emailSender;

  @Autowired
  private AddressRepository addressRepository;

  @GetMapping("/getUsers")
  @ResponseBody
  public List<User> viewCustomPage() {
    List<User> listUsers = (List<User>) userRepository.findAll();

    return listUsers;
  }

  @GetMapping("/getRole")
  @ResponseBody
  public List<Role> viewRole() {
    List<Role> listRole = (List<Role>) roleRepository.findAll();
    System.out.println(listRole.size());

    return listRole;
  }

  @RequestMapping("/webUserRegester_main")
  public String showNewUserFrom(Model model) {
    User customer = new User();
    model.addAttribute("user", customer);
    return "User_Regester";
  }

  @RequestMapping("/AdminUserRegester")
  public String showNewAdminUserFrom(Model model) {
    User customer = new User();
    model.addAttribute("user", customer);
    return "User_Admin_Regester";
  }

  @PostMapping("/sendEmail")
  public void sendingEmail(@Valid @RequestBody VertificationNumber numbers) {
    System.out.println(numbers.getEmail() + " " + numbers.getNumber());
    SimpleMailMessage message = new SimpleMailMessage();
    // message.setFrom("goodstanjo@gmail.com");
    message.setTo(numbers.getEmail());
    message.setSubject("bookstore");
    message.setText("Your Vertification Code is " + numbers.getNumber());
    emailSender.send(message);

  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody User user) throws URISyntaxException {

    User result = userService.saveUser(user);

    return ResponseEntity.created(new URI("/user/register" + result.getId())).body(result);
  }

  @PostMapping("/register_admin")
  public ResponseEntity<?> registerAdminUser(@Valid @RequestBody User user) throws URISyntaxException {

    User result = userService.saveAdminUser(user);

    return ResponseEntity.created(new URI("/user/register_admin" + result.getId())).body(result);
  }

  @PutMapping("/editAUser")
  public ResponseEntity<User> showEditUserFrom(@Valid @RequestBody User user) throws URISyntaxException {

    User result = userService.editUser(user);

    return ResponseEntity.created(new URI("/user/editAUser" + result.getId())).body(result);
  }

  @DeleteMapping("/deleteUser/{user_id}")
  public ResponseEntity<?> deleteUser(@PathVariable(name = "user_id") int user_id) {

    userRepository.deleteById(user_id);
    return ResponseEntity.ok().build();
  }

  // @RequestMapping("/toMain")
  // public String toMain() {

  // return "redirect:/main/mainpage";
  // }

  // @RequestMapping("/searchByemail")
  // public ModelAndView searchByEmail(@RequestParam String keyword) {
  // ModelAndView mav = new ModelAndView("User_search");
  // List<User> result = userRepository.findByEmails(keyword);
  // mav.addObject("result", result);
  // return mav;
  // }

  // @RequestMapping("/searchByLName")
  // public ModelAndView searchByLName(@RequestParam String keyword) {
  // ModelAndView mav = new ModelAndView("User_search");
  // List<User> result = userRepository.findByLName(keyword);

  // mav.addObject("result", result);
  // return mav;
  // }

  // @RequestMapping("/searchByFName")
  // public ModelAndView searchByFName(@RequestParam String keyword) {
  // ModelAndView mav = new ModelAndView("User_search");
  // List<User> result = userRepository.findByFName(keyword);

  // mav.addObject("result", result);
  // return mav;
  // }

  // @RequestMapping("/searchByRole")
  // public ModelAndView searchByRole(@RequestParam String keyword) {
  // ModelAndView mav = new ModelAndView("User_search");
  // List<User> result = userRepository.findByRoles(keyword);

  // mav.addObject("result", result);
  // return mav;
  // }
  @PatchMapping("/addAddress")
  public ResponseEntity<?> addAddress(@Valid @RequestBody User user) throws URISyntaxException {

    User result = userService.addAddress(user);

    return ResponseEntity.created(new URI("/user/addAddress" + result.getId())).body(result);
  }

}

package com.dev.backend.api;

import com.dev.backend.model.User;
import com.dev.backend.payload.request.VertificationNumber;
import com.dev.backend.service.UserService;
import com.dev.backend.service.UserServiceImp;
import com.dev.backend.repository.RoleRepository;
import com.dev.backend.repository.UserRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.dev.backend.model.Role;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;

@CrossOrigin(origins = "https://www.yjportfolio.com")
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

  @GetMapping("/getUsers")
  @ResponseBody
  public List<User> viewCustomPage() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (principal instanceof UserDetails) {

      String username = ((UserDetails) principal).getUsername();
      System.out.println("***currently1 username:" + username);

    } else {

      String username = principal.toString();
      System.out.println("***currently2 username:" + username);

    }
    List<User> listUsers = (List<User>) userRepository.findAll();

    return listUsers;
  }

  @GetMapping("/getCart")
  @ResponseBody
  public User viewCartList() {
    User user = userService.getCurrentUser();
    return user;
  }

  @GetMapping("/getRole")
  @ResponseBody
  public List<Role> viewRole() {

    List<Role> listRole = (List<Role>) roleRepository.findAll();
    System.out.println(listRole.size());

    return listRole;
  }

  @PostMapping("/sendEmail")
  public void sendingEmail(@Valid @RequestBody VertificationNumber numbers) {
    System.out.println(numbers.getEmail() + " " + numbers.getNumber());
    SimpleMailMessage message = new SimpleMailMessage();

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

  @PatchMapping("/addAddress")
  public ResponseEntity<?> addAddress(@Valid @RequestBody User user) throws URISyntaxException {

    User result = userService.addAddress(user);

    return ResponseEntity.created(new URI("/user/addAddress" + result.getId())).body(result);
  }

  @PostMapping("/moveToCart")
  public ResponseEntity<?> moveToCart(@Valid @RequestBody User user) throws URISyntaxException {
    System.out.println("id:" + user.getCart().iterator().next().getId());
    userService.saveInCart(user);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/deleteABook/{id}")
  public ResponseEntity<?> deleteABook(@PathVariable(name = "id") int id) {
    userService.deleteABook(id);
    return ResponseEntity.ok().build();
  }
}

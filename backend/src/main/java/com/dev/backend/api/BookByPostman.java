package com.dev.backend.api;

import com.dev.backend.model.Book;
import com.dev.backend.model.User;
import com.dev.backend.payload.request.ContactMe;
import com.dev.backend.payload.request.LoginRequest;
import com.dev.backend.payload.request.VertificationNumber;
import com.dev.backend.payload.response.JwtResponse;
import com.dev.backend.repository.BookRepository;
import com.dev.backend.repository.RoleRepository;
import com.dev.backend.repository.UserRepository;
import com.dev.backend.security.jwt.JwtUtils;
import com.dev.backend.security.services.UserDetailsImpl;
import com.dev.backend.service.UserService;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.NonNull;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@CrossOrigin(origins = "https://www.yjportfolio.com")
@Controller
@RequestMapping("/api")
public class BookByPostman {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private JavaMailSender emailSender;
  @Autowired
  private UserService userService;
  // For Postman
  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

    System.out.println("role:" + authentication.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
        .collect(Collectors.toList());

    System.out.println(userDetails.getUsername());

    return ResponseEntity
        .ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getName(), roles));
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody User user) throws URISyntaxException {

    User result = userService.saveUser(user);

    return ResponseEntity.created(new URI("/user/register" + result.getId())).body(result);
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

  @PostMapping("/contactme")
  public void contactMe(@Valid @RequestBody ContactMe contact) {
    System.out.println(contact.getName());
    SimpleMailMessage message = new SimpleMailMessage();

    message.setTo("goodstanjo@gmail.com");
    message.setSubject(contact.getName() + " contacted me from my App");
    message.setText("Name:" + contact.getName() + "\n" + "Phone Number: " + contact.getPhoneNumber() + "\n" + "email: "
        + contact.getEmail() + "\n" + "Message" + "\n\n" + contact.getMsg());
    emailSender.send(message);

  }

  // @PostMapping("/register")
  // public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest
  // signUpRequest) throws URISyntaxException {
  // System.out.println(signUpRequest);
  // User result = userRepository.findByEmail2(signUpRequest.getEmail());

  // return ResponseEntity.created(new URI("/api/register" +
  // result.getId())).body(result);
  // }

  @PostMapping("/addBook")
  @ResponseBody
  public String addAll(@RequestBody List<Book> Books) {
    bookRepository.saveAll(Books);
    return "person size:" + Books.size();

  }

  @GetMapping("/postman/getBook")
  @ResponseBody
  public List<Book> getBook() {
    return (List<Book>) bookRepository.findAll();
  }

  @GetMapping("/postman/getUser")
  @ResponseBody
  public List<User> getUser() {
    return (List<User>) userRepository.findAll();
  }

  @GetMapping(path = "/postman/{id}")
  @ResponseBody
  public Optional<Book> getBookbyid(@PathVariable("id") int id) {
    ArrayList<Book> result = new ArrayList<>();
    bookRepository.findById(id).ifPresent(result::add);

    return bookRepository.findById(id);
  }

  @DeleteMapping(path = "/postman/{id}")
  @ResponseBody
  public void deleteBookById(@PathVariable("id") int id) {
    bookRepository.deleteById(id);
  }

  @PutMapping("/postman/put/{id}")
  @ResponseBody
  public void putBookById(@PathVariable("id") int id, @Valid @NonNull @RequestBody Book bookToUpdate) {

    bookToUpdate.setId(id);
    bookRepository.save(bookToUpdate);
  }

}

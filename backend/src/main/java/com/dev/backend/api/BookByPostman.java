package com.dev.backend.api;

import com.dev.backend.model.Book;
import com.dev.backend.model.User;
import com.dev.backend.payload.request.LoginRequest;

import com.dev.backend.payload.response.JwtResponse;
import com.dev.backend.repository.BookRepository;
import com.dev.backend.repository.RoleRepository;
import com.dev.backend.repository.UserRepository;
import com.dev.backend.security.jwt.JwtUtils;
import com.dev.backend.security.services.UserDetailsImpl;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        .ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
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

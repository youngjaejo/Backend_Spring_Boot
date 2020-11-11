package com.dev.backend.service;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dev.backend.model.Address;
import com.dev.backend.model.Book;
import com.dev.backend.model.Role;
import com.dev.backend.model.User;

import com.dev.backend.repository.UserRepository;
import com.dev.backend.repository.AddressRepository;
import com.dev.backend.repository.BookRepository;
import com.dev.backend.repository.RoleRepository;

@Service
public class UserServiceImp implements UserService {

	@Autowired
	BCryptPasswordEncoder encoder;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	BookRepository bookRepository;
	@Autowired
	AddressRepository addressRepository;

	@Override
	public User saveUser(User user) {

		user.setDateRegisterated(Instant.now());
		user.setPassword(encoder.encode(user.getPassword()));

		Role userRole = roleRepository.findByRole("SITE_USER");
		user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		User result = userRepository.save(user);
		return result;
	}

	@Override
	public User saveAdminUser(User user) {
		user.setPassword(encoder.encode(user.getPassword()));

		Role userRole = roleRepository.findByRole("ADMIN_USER");
		user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		User result = userRepository.save(user);
		return result;
	}

	@Override
	public User editUser(User user) {
		User userUpdate = userRepository.getOne(user.getId());
		// userUpdate.setFirstName(user.getFirstName());
		// userUpdate.setLastName(user.getLastName());
		// userUpdate.setEmail(user.getEmail());
		if (!(user.getPassword().isEmpty())) {
			userUpdate.setPassword(encoder.encode(user.getPassword()));
		}
		// Optional<User> originalUser = userRepository.findById(user.getId());

		String listU = userRepository.findRoleByUser(user.getEmail());
		Role userRole = roleRepository.findByRole(listU);
		userUpdate.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		userUpdate.setFirstName(user.getFirstName());
		userUpdate.setLastName(user.getLastName());
		User result = userRepository.save(userUpdate);
		return result;
	}

	@Override
	public User addAddress(User user) {
		User userUpdate = userRepository.getOne(user.getId());
		Address address = new Address();
		address.setAddress(user.getAddresses().iterator().next().getAddress());
		address.setCity(user.getAddresses().iterator().next().getCity());
		address.setState(user.getAddresses().iterator().next().getState());
		address.setZipCode(user.getAddresses().iterator().next().getZipCode());
		address.setUser(userUpdate);
		// userUpdate.setAddress(new HashSet<Address>(Arrays.asList(address)));
		userUpdate.getAddresses().add(address);
		User result = userRepository.save(userUpdate);
		return result;

	}

	@Override
	public boolean isUserAlreadyPresent(User user) {

		User email = userRepository.findByAEmail(user.getEmail());
		if (email != null)
			return true;
		else
			return false;
	}

	@Override
	public void saveInCart(User user) {
		int bookId = user.getCart().iterator().next().getId();
		User temUser = userRepository.getOne(user.getId());
		Book book = bookRepository.getOne(bookId);
		temUser.addCart(new HashSet<Book>(Arrays.asList(book)));
		userRepository.save(temUser);
	}

	@Override
	public User getCurrentUser() {

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = ((UserDetails) principal).getUsername();
		User user = userRepository.findByAEmail(userName);
		return user;

	}

	@Override
	public void deleteABook(int id) {
		User user = getCurrentUser();
		Book book = bookRepository.getOne(id);
		user.getCart().remove(book);
		userRepository.save(user);

	}
}

package com.dev.backend.service;

import com.dev.backend.model.*;

public interface UserService {

	public User saveUser(User user);

	public User saveAdminUser(User user);

	public boolean isUserAlreadyPresent(User user);

	public User editUser(User user);

	public User addAddress(User user);

	public void saveInCart(User user);

	public User getCurrentUser();

	public void deleteABook(int id);
}

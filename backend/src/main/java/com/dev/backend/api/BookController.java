package com.dev.backend.api;

import com.amazonaws.AmazonServiceException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dev.backend.model.Book;
import com.dev.backend.model.User;
import com.dev.backend.repository.BookRepository;
import com.dev.backend.service.UserService;

import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "https://www.yjportfolio.com")
@Controller
@RequestMapping("/books")
public class BookController {
  public static String uploadDirectory = System.getProperty("user.dir") + "/backend/src/main/resources/static/images";
  private AmazonS3 s3Client;
  @Value("${s3.endpointUrl}")
  private String endpointUrl;
  @Value("${s3.accessKey}")
  private String accessKey;
  @Value("${s3.secertKey}")
  private String secretKey;
  @Value("${s3.bucketName}")
  private String bucketName;

  @Autowired
  private BookRepository bookRepository;

  @GetMapping("/showAllBooks")
  @ResponseBody
  public List<Book> viewHomePage() {
    List<Book> listBooks = (List<Book>) bookRepository.findAll();
    return listBooks;
  }

  @PostConstruct
  private void initializeAmazom() {
    AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
    this.s3Client = new AmazonS3Client(credentials);
  }

  @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Book> saveBook(@RequestPart("book") @Valid Book book,
      @RequestPart("file") @Valid MultipartFile file)
      // (@Valid @RequestBody Book book, @RequestParam("file") MultipartFile file)
      throws URISyntaxException, IOException {

    try {
      System.out.print(book.getAuthor());
      String fileName = file.getOriginalFilename();
      System.out.print(fileName);
      File convFile = new File(fileName);
      FileOutputStream fos = new FileOutputStream(convFile);
      fos.write(file.getBytes());
      fos.close();

      s3Client.putObject(
          new PutObjectRequest(bucketName, fileName, convFile).withCannedAcl(CannedAccessControlList.PublicRead));

      book.setImg_name(fileName);
      Book result = bookRepository.save(book);
      convFile.delete();
      return ResponseEntity.created(new URI("/books/save" + result.getId())).body(result);
    } catch (AmazonServiceException are) {
      System.out.println("aws error");
      return ResponseEntity.ok().build();
    }

  }

  @PostMapping(value = "/editBook", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Book> editBook(@RequestPart("book") @Valid Book book,
      @RequestPart("file") @Valid MultipartFile file) throws URISyntaxException, IOException {
    try {
      if (file.getOriginalFilename().isEmpty()) {

        Book result = bookRepository.save(book);
        return ResponseEntity.created(new URI("/books/editBook" + result.getId())).body(result);
      }

      s3Client.deleteObject(new DeleteObjectRequest(bucketName, book.getImg_name()));
      String fileName = file.getOriginalFilename();
      File convFile = new File(fileName);
      FileOutputStream fos = new FileOutputStream(convFile);
      fos.write(file.getBytes());
      fos.close();
      s3Client.putObject(
          new PutObjectRequest(bucketName, fileName, convFile).withCannedAcl(CannedAccessControlList.PublicRead));

      book.setImg_name(fileName);
      Book result = bookRepository.save(book);
      convFile.delete();
      return ResponseEntity.created(new URI("/books/editBook" + result.getId())).body(result);

    } catch (AmazonServiceException are) {
      System.out.println("aws error");
      return ResponseEntity.ok().build();
    } catch (IOException a) {
      return ResponseEntity.ok().build();
    }

  }

  @PutMapping(value = "/editBook_noFile")
  public ResponseEntity<Book> editBook(@RequestBody @Valid Book book) throws URISyntaxException {

    Book result = bookRepository.save(book);
    return ResponseEntity.created(new URI("/books/editBook" + result.getId())).body(result);

  }

  @DeleteMapping("/deleteABook/{id}")
  public ResponseEntity<?> deleteBook(@PathVariable(name = "id") int id) throws URISyntaxException {
    Book book = bookRepository.getOne(id);
    String imgName = book.getImg_name();
    s3Client.deleteObject(new DeleteObjectRequest(bucketName, imgName));

    bookRepository.deleteById(id);

    return ResponseEntity.ok().build();
  }

  // @GetMapping("/searchByTitle/{keyword}")
  // @ResponseBody
  // public List<Book> searchByTitle(@PathVariable(name = "keyword") String
  // keyword) {
  // System.out.println("aa");
  // List<Book> result = bookRepository.searchByTitle(keyword);

  // return result;
  // }

}

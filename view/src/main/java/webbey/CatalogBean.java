package webbey;


import webbey.domain.Book;
import webbey.ejb.BookManagerBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.RowEditEvent;



@Named("catalogBean")
@SessionScoped
public class CatalogBean implements Serializable {

    private long id;
    private String name;
    private String author;
    private int publishingYear;
    private int quantityTotal;
    boolean searchBooks;
    List<Book> books;


    @EJB
    private BookManagerBean bookManagerBean;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPublishingYear() {
        return publishingYear;
    }

    public void setPublishingYear(int publishingYear) {
        this.publishingYear = publishingYear;
    }

    public int getQuantityTotal() {
        return quantityTotal;
    }

    public void setQuantityTotal(int quantityTotal) {
        this.quantityTotal = quantityTotal;
    }

    public void createBook(){
        bookManagerBean.createBook(name, author,publishingYear, quantityTotal);
        setSearchBooks(true);
    }

    public void incrementQuantityTotal(Book book){
        bookManagerBean.incrementQuantityTotal(book);
    }
    //TODO Messaging max
    public void incrementQuantityBalance(Book book){
        bookManagerBean.incrementQuantityBalance(book);
        books = refreshBooks();
    }

    public void decrementQuantityTotal(Book book){
        bookManagerBean.decrementQuantityTotal(book);
    }
    //TODO Messaging min
    public void decrementQuantityBalance(Book book){
        bookManagerBean.decrementQuantityBalance(book);
        books = refreshBooks();
    }

    public boolean isSearchBooks() {
        return searchBooks;
    }

    public List<Book> getBooks() {
        return books;
    }

    @PostConstruct
    public void init() {
        books = refreshBooks();

    }

    public void setSearchBooks(boolean searchBooks) {
        this.searchBooks = searchBooks;
        books = refreshBooks();
    }



    public List<Book> refreshBooks(){

        if (searchBooks){
            return bookManagerBean.searchBooks(id, name, author, publishingYear);
        }
        else{
            return bookManagerBean.getBooks();
        }
    }

    public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Книга отредактирована",((Book)event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        bookManagerBean.editBook((Book) event.getObject());
        books = refreshBooks();
    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Редактирование отменено",((Book) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}

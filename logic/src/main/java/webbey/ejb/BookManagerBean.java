package webbey.ejb;


import webbey.domain.Book;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Null;
import java.util.List;

@Stateless
@LocalBean
public class BookManagerBean {
//JPA
    @PersistenceContext(unitName = "examplePU")
    private EntityManager entityManager;


    public Book createBook(String name, String author,int publishingYear, int quantityTotal){

        Book book = new Book();
        book.setName(name);
        book.setAuthor(author);
        book.setPublishingYear(publishingYear);
        book.setQuantityBalance(quantityTotal); //изначально ставим значение как у Total.
        book.setQuantityTotal(quantityTotal);
        entityManager.persist(book);
        return book;
    }

    public List<Book> getBooks(){
       TypedQuery<Book> typedQuery = entityManager.createQuery("select c from Book c order by c.id",Book.class);
       return typedQuery.getResultList();
    }
    public List<Book> searchBooks(long id,String name,String author,int publishingYear) {
        boolean flagWhere = false;
        StringBuilder s = new StringBuilder();
        if(!name.isEmpty()){
            s.append("WHERE (c.name LIKE :nameParam)");
            flagWhere = true;
        }
        if(!author.isEmpty()){
            if(flagWhere) {
                s.append(" AND (c.author LIKE :authorParam)");
            }
            else{
                s.append("WHERE (c.author LIKE :authorParam)");
                flagWhere = true;
           }
        }
        if(publishingYear!= 0){
            if(flagWhere) {
                s.append(" AND (c.publishingYear=:publishingYearParam)");
            }
            else{
                s.append("WHERE (c.publishingYear=:publishingYearParam)");
                flagWhere = true;
            }
        }
        TypedQuery<Book> typedQuery = entityManager.createQuery("select c from Book c "+ s + "  order by c.id",Book.class);
        if(!name.isEmpty()){
            typedQuery.setParameter("nameParam", name);
        }
        if(!author.isEmpty()){
                typedQuery.setParameter("authorParam", author);
        }
        if(publishingYear!= 0){
            typedQuery.setParameter("publishingYearParam", publishingYear);
        }

        return typedQuery.getResultList();
    }

    //TODO sysnc add
    public void incrementQuantityTotal(Book book){
        Book b = entityManager.find(Book.class,book.getId());
        b.setQuantityTotal(b.getQuantityTotal()+1);

    }
    public void incrementQuantityBalance(Book book){
        Book b = entityManager.find(Book.class,book.getId());
        if(b.getQuantityBalance() < b.getQuantityTotal()) {
            b.setQuantityBalance(b.getQuantityBalance() + 1);
        }
    }

    public void decrementQuantityTotal(Book book){
        Book b = entityManager.find(Book.class,book.getId());
        if(b.getQuantityTotal() > 0) {
            b.setQuantityTotal(b.getQuantityTotal()-1);
        }

    }
    public void decrementQuantityBalance(Book book){
        Book b = entityManager.find(Book.class,book.getId());
        if(b.getQuantityBalance() > 0) {
            b.setQuantityBalance(b.getQuantityBalance() - 1);
        }
    }


    public void editBook(Book book) {
        entityManager.merge(book);
    }
}

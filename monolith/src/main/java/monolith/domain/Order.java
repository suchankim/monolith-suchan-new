package monolith.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import monolith.MonolithApplication;
import monolith.domain.OrderPlaced;

@Entity
@Table(name = "Order_table")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String productId;

    private Integer qty;

    private Integer customerId;

    private Long amount;

    @PostPersist
    public void onPostPersist() {
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        monolith.external.DecreaseStockCommand decreaseStockCommand = new monolith.external.DecreaseStockCommand();
        // mappings goes here
        MonolithApplication.applicationContext
            .getBean(monolith.external.InventoryService.class)
            .decreaseStock(/* get???(), */decreaseStockCommand);

        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publishAfterCommit();
    }

    public static OrderRepository repository() {
        OrderRepository orderRepository = MonolithApplication.applicationContext.getBean(
            OrderRepository.class
        );
        return orderRepository;
    }
}

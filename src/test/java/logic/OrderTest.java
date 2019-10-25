package logic;

import org.junit.Before;
import org.junit.Test;
import persistence.OrderException;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class OrderTest {
    private Order order;
    private LineItem item;

    @Before
    public void setUp() {
        order = new Order(LocalDate.of(2000,2,1));
        item = new LineItem(new Cupcake(
                        new Bottom(20,"Blueberry"),
                        new Topping(20,"Chocolate")), 2);
        order.addLineItem(item);
    }

    @Test
    public void test_addLineItem() {
        int size = order.getSize();
        order.addLineItem(new LineItem(new Cupcake(null,null),2));
        assertEquals(size+1, order.getSize());
    }

    @Test
    public void test_removeLineItem() {
        int size = order.getSize();
        order.removeLineItem(item);
        assertEquals(size-1, order.getSize());
    }

    @Test
    public void test_RemoveLineItem_by_index() throws OrderException {
        int size = order.getSize();
        order.removeLineItem(0);
        assertEquals(size-1, order.getSize());
    }

    @Test
    public void test_RemoveLineItem_non_existing_item() {
        int size = order.getSize();
        order.removeLineItem(new LineItem(null,2));
        assertEquals(size,order.getSize());
    }

    @Test(expected = OrderException.class)
    public void test_RemoveLineItem_non_existing_index() throws OrderException {
        int size = order.getSize();
        order.removeLineItem(2000);
    }

    @Test
    public void test_getLineItem() throws OrderException {
        assertEquals(item, order.getLineItem(0));
    }

    @Test
    public void test_getTotalQuantity() {
        int exp = item.getQuantity()*2;
        order.addLineItem(item);
        int res = order.getTotalQuantity();
        assertEquals(exp,res);
    }

    @Test
    public void getSize() {
        assertEquals(1,order.getSize());
    }
}
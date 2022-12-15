import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestBasket {
    @Test
    public void testBasketsPrice(){
        Basket basket = getNewBasket();
        Assertions.assertArrayEquals(basket.getPrice(), new int[]{1,4,5,9,53});
    }

    public Basket getNewBasket(){
        return new Basket(new int[]{1,4,5,9,53},
                new String[]{"Хлеб", "Масло", "Чай", "Вода", "Колбаса"});
    }
    @Test
    public void testBasketSetBasket(){
        Basket basket = getNewBasket();
        basket.setBaskets(new int[]{1,2,3});
        Assertions.assertFalse(basket.getBaskets().length == 0);
    }

    @Test
    public void testBasketSumFood(){
        Basket basket = getNewBasket();
        basket.setBaskets(new int[]{1,2,3});
        Assertions.assertTrue(basket.getSumFood() == 24);
    }

    @Test
    public void testFoundFileSettings(){
        Assertions.assertTrue(new File("shop.xml").exists());
    }
}

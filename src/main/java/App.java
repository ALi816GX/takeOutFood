import java.util.ArrayList;
import java.util.List;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {
    private ItemRepository itemRepository;
    private SalesPromotionRepository salesPromotionRepository;

    public App(ItemRepository itemRepository, SalesPromotionRepository salesPromotionRepository) {
        this.itemRepository = itemRepository;
        this.salesPromotionRepository = salesPromotionRepository;
    }

    public String bestCharge(List<String> inputs) {

        List<Order> orderList = new ArrayList<>();

        StringBuilder result = new StringBuilder("============= 订餐明细 =============\n");

        for (String one:inputs){

            int spaceIndex = one.indexOf(" ");
            int nextSpaceIndex = one.lastIndexOf(" ")+1;

            String foodId = one.substring(0,spaceIndex);
            int count = Integer.parseInt(one.substring(nextSpaceIndex));
            double price = 0;
            String foodName = "";

            for(Item item:itemRepository.findAll()){
                if(item.getId().equals(foodId)){
                    price = item.getPrice();
                    foodName = item.getName();
                    orderList.add(new Order(item,count));
                    break;
                }
            }

            String details = getOneFoodDetails(foodName,count,price);
            result.append(details);
        }

        result.append(getTotalDiscount(orderList));

        System.out.println(result);

        return result.toString();
    }

    /**
     * 得到每一条货品订单细节
     * @param foodName  货名
     * @param count     数量
     * @param price     单价
     * @return
     */
    public String getOneFoodDetails(String foodName,int count,double price){

        StringBuilder result = new StringBuilder();
        result.append(foodName).append(" x ").append(count).append(" = ");
        int total = (int) (count * price);
        result.append(total).append("元\n");

        return result.toString();

    }


    /**
     *
     * @param orders 订单列表
     * @return
     */
    public String getTotalDiscount(List<Order> orders){

        StringBuilder result = new StringBuilder();

        int total = 0;
        int discount1 = 0;
        int discount2 = 0;
        String discountItemNames = "";

        List<SalesPromotion> salesPromotions = salesPromotionRepository.findAll();
        List<String> relatedItems = salesPromotions.get(1).getRelatedItems();

        //择品半价
        for (Order order:orders){
            String id = order.item.getId();
            double price =  order.getItem().getPrice();
            int count = order.count;
            for(String one:relatedItems){
                if(one.equals(id)){
                    discountItemNames += order.item.getName()+"，";
                    discount2 += price * count / 2;
                }
            }
            total += price * count;
        }

        if(!discountItemNames.equals("")) {
            discountItemNames = discountItemNames.substring(0, discountItemNames.length() - 1);
        }

        //满30减6
        if(total>=30){
            discount1 = 6;
        }


        //方案不优惠
        if(discount1 == 0 && discount2 == 0){ }

        //方案1
        else if(discount1 >= discount2){
            total = total - discount1;
            result.append("-----------------------------------\n使用优惠:\n")
                    .append(salesPromotions.get(0).getDisplayName()).append("，省")
                    .append(discount1).append("元\n");
        }

        //方案2
        else if(discount1 < discount2){
            total = total - discount2;
            result.append("-----------------------------------\n使用优惠:\n")
                    .append(salesPromotions.get(1).getDisplayName())
                    .append("(").append(discountItemNames).append(")，省")
                    .append(discount2).append("元\n");
        }

        result.append("-----------------------------------\n")
                .append("总计：").append(total).append("元\n")
                .append("===================================");

        return result.toString();
    }




    class Order {     //内部类

        private Item item;
        private int count;

        public Order(Item item, int count) {
            this.item = item;
            this.count = count;
        }

        public Item getItem() {
            return item;
        }

        public void setItem(Item item) {
            this.item = item;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }



}

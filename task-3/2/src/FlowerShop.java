import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

public class FlowerShop {

    public static void main(String[] args) {
        FlowerShop shop = new FlowerShop();
        Bouquet bouquet = shop.generateRandomBouquet();
        shop.printReceipt(bouquet);
    }

    private void printReceipt(Bouquet bouquet) {
        int totalWidth = 65;

        System.out.println("=".repeat(totalWidth));
        System.out.println(centerString("FLOWER SHOP RECEIPT", totalWidth));
        System.out.println("=".repeat(totalWidth));

        WrappingType wrappingType = bouquet.getWrappingType();
        System.out.printf(" Wrapping: %-45s %3.2f $\n", wrappingType, wrappingType.getPrice());
        System.out.println("-".repeat(totalWidth));

        System.out.println(" Bouquet composition:");
        System.out.println("-".repeat(totalWidth));
        Map<Flower, Integer> flowers = bouquet.getFlowers();
        List<Map.Entry<Flower, Integer>> sortedFlowers = flowers.entrySet().stream()
            .sorted((a, b) -> a.getKey().getClass().getSimpleName()
                    .compareTo(b.getKey().getClass().getSimpleName()))
            .toList();
        for (Map.Entry<Flower, Integer> flowerAndCount : sortedFlowers) {
            Flower flower = flowerAndCount.getKey();
            int flowerCount = flowerAndCount.getValue();
            BigDecimal flowerPrice = flower.getPrice();
            BigDecimal subsumPrice = flowerPrice.multiply(BigDecimal.valueOf(flowerCount));
            System.out.printf("    %-25s %2.2f $  ×  %3d pcs  =  %8.2f $\n",
                    flower, flowerPrice, flowerCount, subsumPrice.doubleValue());
        }

        double bouquetPrice = bouquet.getPrice().doubleValue();
        Optional<Integer> optionalFlowersCount = bouquet.getFlowers().values().stream().reduce(Integer::sum);
        if (optionalFlowersCount.isEmpty())
            throw new IllegalStateException("Flowers bouquet cannot be empty");
        int flowersCount = optionalFlowersCount.get();
        System.out.println("=".repeat(totalWidth));
        System.out.printf(" TOTAL AMOUNT TO BE PAID: %18d pcs  =  %8.2f $\n", flowersCount, bouquetPrice);

        System.out.println("=".repeat(totalWidth));
        System.out.println(centerString("Thanks for the purchase!", totalWidth));
    }

    private static String centerString(String source, int totalWidth) {
        int width = source.length();
        int emptySpaceLength = totalWidth - width;
        int prefixLength = emptySpaceLength / 2;
        return " ".repeat(prefixLength) + source;
    }

    private Class<? extends Flower> generateRandomFlowerClass() {
        double p = new Random().nextDouble();
        if (p < 0.5) return Rose.class;
        else if (p < 0.8) return Tulip.class;
        else return Chrysanthemum.class;
    }

    private FlowerColor generateRandomRoseColor() {
        double p = new Random().nextDouble();
        if (p < 0.40) return FlowerColor.RED;
        else if (p < 0.60) return FlowerColor.WHITE;
        else if (p < 0.75) return FlowerColor.PINK;
        else if (p < 0.85) return FlowerColor.YELLOW;
        else if (p < 0.95) return FlowerColor.ORANGE;
        else return FlowerColor.PURPLE;
    }

    private FlowerColor generateRandomTulipColor() {
        double p = new Random().nextDouble();
        if (p < 0.35) return FlowerColor.RED;
        else if (p < 0.60) return FlowerColor.YELLOW;
        else if (p < 0.80) return FlowerColor.WHITE;
        else if (p < 0.90) return FlowerColor.PINK;
        else if (p < 0.95) return FlowerColor.ORANGE;
        else return FlowerColor.PURPLE;
    }

    private FlowerColor generateRandomChrysanthemumColor() {
        double p = new Random().nextDouble();
        if (p < 0.40) return FlowerColor.WHITE;
        else if (p < 0.75) return FlowerColor.YELLOW;
        else if (p < 0.85) return FlowerColor.RED;
        else if (p < 0.95) return FlowerColor.PINK;
        else return FlowerColor.PURPLE;
    }

    private Flower generateRandomFlower() {
        Class<? extends Flower> flowerClass = generateRandomFlowerClass();
        FlowerColor color;
        if (flowerClass.equals(Rose.class)) color = generateRandomRoseColor();
        else if (flowerClass.equals(Tulip.class)) color = generateRandomTulipColor();
        else if (flowerClass.equals(Chrysanthemum.class)) color = generateRandomChrysanthemumColor();
        else throw new UnsupportedOperationException("Color distribution of " + flowerClass.getName().toLowerCase() +
                    " flower is unreleased");
        try {
            Constructor<? extends Flower> constructor = flowerClass.getConstructor(FlowerColor.class);
            try {
                return constructor.newInstance(color);
            } catch (UnsupportedFlowerColorException | InstantiationException | IllegalAccessException |
                     IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private int generateRandomBouquetSize() {
        Random random = new Random();
        double p = random.nextDouble();
        if (p < 0.5) return 3 + random.nextInt(3); // 3..5
        else if (p < 0.9) return 6 + random.nextInt(7); // 6..12
        else return 13 + new Random().nextInt(8); // 13..20
    }

    private WrappingType generateRandomBouquetWrappingType() {
        double p = new Random().nextDouble();
        if (p < 0.30) return WrappingType.PAPER;
        else if (p < 0.50) return WrappingType.FILM;
        else if (p < 0.65) return WrappingType.CRAFT_PAPER;
        else if (p < 0.80) return WrappingType.ORGANZA;
        else if (p < 0.90) return WrappingType.SATIN;
        else return WrappingType.GIFT_BOX;
    }

    public Bouquet generateRandomBouquet() {
        Map<Flower, Integer> flowers = new HashMap<>();
        int count = generateRandomBouquetSize();
        for (int i = 0; i < count; ++i) {
            Flower flower = generateRandomFlower();
            flowers.put(flower, flowers.getOrDefault(flower, 0) + 1);
        }
        WrappingType wrappingType = generateRandomBouquetWrappingType();
        return new Bouquet(flowers, wrappingType);
    }
}
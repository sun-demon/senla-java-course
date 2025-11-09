import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;

public class Bouquet {
    private final Map<Flower, Integer> flowers;
    private final WrappingType wrappingType;

    public Bouquet(Map<Flower, Integer> flowers, WrappingType wrappingType) {
        this.flowers = new HashMap<>();
        flowers.forEach((flower, count) -> {
            if (count < 0) {
                throw new IllegalArgumentException("The number of " + flower.toString() + " cannot be negative");
            } else if (count > 0) {
                this.flowers.put(flower, count);
            }
        });
        if (flowers.isEmpty())
            throw new IllegalStateException("Bouquet cannot be empty");
        this.wrappingType = wrappingType;
    }

    public BigDecimal getPrice() {
        Optional<BigDecimal> flowersPrice = flowers.entrySet().stream()
                .map((flowerAndCount) ->
                        flowerAndCount.getKey().getPrice().multiply(BigDecimal.valueOf(flowerAndCount.getValue())))
                .reduce(BigDecimal::add);
        if (flowersPrice.isEmpty())
            throw new IllegalStateException("Bouquet cannot be empty");
        return wrappingType.getPrice().add(flowersPrice.get());
    }

    public Map<Flower, Integer> getFlowers() {
        return flowers;
    }

    public WrappingType getWrappingType() {
        return wrappingType;
    }
}
import java.lang.Math;

class ThreeDigitSumCalculator {
    public static void main(String[] args) {
        int randomNumber = generateThreeDigitPositiveInt();
        int digitSum = calculateDigitSum(randomNumber);
        System.out.println("Generated number: " + randomNumber);
        System.out.println("Digits sum: " + digitSum);
    }

    private static int generateThreeDigitPositiveInt() {
        return 100 + (new java.util.Random()).nextInt(900);
    }

    private static int calculateDigitSum(int number) {
        int sum = 0;

        while (number != 0) {
            sum += number % 10;
            number /= 10;
        }

        return Math.abs(sum);
    }
}
package org.cau02.model;
import org.cau02.model.interfaces.YutStickBehavior;
import java.util.Random;

/** 윷 던지는 행위를 담당 */
public class RandomYutStick implements YutStickBehavior {
    private final Random random = new Random();

    // 무작위로 윷 결과 반환
    @Override
    public YutResult throwSticks() {
        int result = random.nextInt(5); // 확률 조정을 위한 임시 값
        switch (result) {
            case 0:
                return YutResult.BACK_DO; // 백도
            case 1:
                return YutResult.MO;      // 모
            case 2:
                return YutResult.YUT;     // 윷
            case 3:
                return YutResult.GEOL;    // 걸
            case 4:
                return YutResult.GAE;     // 개
            default:
                return YutResult.DO;      // 도
        }
    }
}
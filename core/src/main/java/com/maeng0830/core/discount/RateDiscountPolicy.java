package com.maeng0830.core.discount;

import com.maeng0830.core.annotation.MainDiscountPolicy;
import com.maeng0830.core.member.Grade;
import com.maeng0830.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
// @Qualifier("mainDiscountPolicy") // 동일 타입의 빈이 여러개일 때, Qualifier를 추가 식별자로 사용할 수 있다.
//  그러나, 컴파일 오류로 확인할 수 없다는 단점이 있다. -> 별도의 annotation을 생성해서 사용하자!
@MainDiscountPolicy
// @Primary // 동일 타입의 빈이 여러개일 때, Primary 빈이 우선적으로 주입된다.
public class RateDiscountPolicy implements DiscountPolicy {

    private int discountPercent = 10;

    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP) {
            return price * discountPercent / 100;
        } else {
            return 0;
        }
    }
}

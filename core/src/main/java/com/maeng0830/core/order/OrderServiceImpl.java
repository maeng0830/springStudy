package com.maeng0830.core.order;

import com.maeng0830.core.discount.DiscountPolicy;
import com.maeng0830.core.member.Member;
import com.maeng0830.core.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor // 성자를 통해 반드시 초기화 되어야 하는 final 필드에 대해 생성자를 추가해준다.
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    // 테스트
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}

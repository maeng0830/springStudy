package com.maeng0830.core.order;

import com.maeng0830.core.discount.DiscountPolicy;
import com.maeng0830.core.discount.FixDiscountPolicy;
import com.maeng0830.core.member.Member;
import com.maeng0830.core.member.MemberRepository;
import com.maeng0830.core.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository = new MemoryMemberRepository();
    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}

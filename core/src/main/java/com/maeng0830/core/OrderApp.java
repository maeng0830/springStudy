package com.maeng0830.core;

import com.maeng0830.core.member.Grade;
import com.maeng0830.core.member.Member;
import com.maeng0830.core.member.MemberService;
import com.maeng0830.core.member.MemberServiceImpl;
import com.maeng0830.core.order.Order;
import com.maeng0830.core.order.OrderService;
import com.maeng0830.core.order.OrderServiceImpl;

public class OrderApp {

    public static void main(String[] args) {
        MemberService memberService = new MemberServiceImpl();
        OrderService orderService = new OrderServiceImpl();

        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "itemA", 10000);

        System.out.println("order = " + order);
    }

}

package mate.academy.spring.controller;

import java.util.List;
import mate.academy.spring.dto.response.OrderResponseDto;
import mate.academy.spring.model.Order;
import mate.academy.spring.model.ShoppingCart;
import mate.academy.spring.model.User;
import mate.academy.spring.service.OrderService;
import mate.academy.spring.service.ShoppingCartService;
import mate.academy.spring.service.UserService;
import mate.academy.spring.service.mapper.ResponseDtoMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final ShoppingCartService shoppingCartService;
    private final OrderService orderService;
    private final UserService userService;
    private final ResponseDtoMapper<OrderResponseDto, Order> orderResponseDtoMapper;

    public OrderController(ShoppingCartService shoppingCartService,
                           OrderService orderService,
                           UserService userService,
                           ResponseDtoMapper<OrderResponseDto, Order> orderResponseDtoMapper) {
        this.shoppingCartService = shoppingCartService;
        this.orderService = orderService;
        this.userService = userService;
        this.orderResponseDtoMapper = orderResponseDtoMapper;
    }

    @PostMapping("/complete")
    public OrderResponseDto completeOrder(@RequestParam Authentication authentication) {
        User user = userService.findByEmail(authentication.getName()).orElseThrow(
                () -> new RuntimeException("Can't complete by user"));
        ShoppingCart cart = shoppingCartService.getByUser(userService.get(user.getId()));
        return orderResponseDtoMapper.mapToDto(orderService.completeOrder(cart));
    }

    @GetMapping
    public List<OrderResponseDto> getOrderHistory(@RequestParam Authentication authentication) {
        User user = userService.findByEmail(authentication.getName()).orElseThrow(
                () -> new RuntimeException("Can't get order history by user"));;
        return orderService.getOrdersHistory(userService.get(user.getId()))
                .stream()
                .map(orderResponseDtoMapper::mapToDto)
                .toList();
    }
}

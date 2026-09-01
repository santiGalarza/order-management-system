package com.santiGalarza.order_management.order.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderStatusServiceTest {

    @Mock
    private OrderStatusRepository statusRepository;

    @Mock
    private OrderStatusTransitionRepository transitionRepository;

    @Mock
    private OrderStatusHistoryRepository historyRepository;

    @InjectMocks
    private OrderStatusService orderStatusService;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("throws when status code not found")
    void throwsWhenStatusCodeNotFound() {

    }

}

package com.codecampus.identity.configuration.audit;

import com.codecampus.identity.entity.audit.SoftDeletable;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;

@Slf4j
public class SoftDeleteInterceptor
        extends EmptyInterceptor {
    @Override
    public void onDelete(
            Object entity,
            Object id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {
        if (entity instanceof SoftDeletable soft) {
            // Chặn DELETE -> UPDATE
            soft.markDeleted(SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName());

            // Đẩy giá trị vào state[] để Hibernate cập nhật DB
            for (int i = 0; i < propertyNames.length; i++) {
                switch (propertyNames[i]) {
                    case "deletedBy" -> state[i] = soft.getDeletedBy();
                    case "deletedAt" -> state[i] = Instant.now();
                }
            }
        } else {
            super.onDelete(entity, id, state, propertyNames, types);
        }
    }
}

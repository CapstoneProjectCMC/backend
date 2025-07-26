package com.codecampus.identity.service.account;

import com.codecampus.identity.dto.request.authentication.PermissionRequest;
import com.codecampus.identity.dto.response.authentication.PermissionResponse;
import com.codecampus.identity.entity.account.Permission;
import com.codecampus.identity.mapper.authentication.PermissionMapper;
import com.codecampus.identity.repository.account.PermissionRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Dịch vụ quản lý quyền (Permission) trong hệ thống.
 *
 * <p>Cung cấp các phương thức để:
 * <ul>
 *   <li>Tạo quyền mới.</li>
 *   <li>Lấy danh sách tất cả quyền.</li>
 *   <li>Xóa quyền theo mã quyền.</li>
 * </ul>
 * Các phương thức chỉ cho phép thực thi khi người dùng có vai trò ADMIN.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    /**
     * Tạo mới một quyền trong hệ thống.
     *
     * @param request đối tượng PermissionRequest chứa thông tin quyền cần tạo
     * @return PermissionResponse chứa thông tin quyền vừa được lưu
     */
    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    /**
     * Lấy danh sách tất cả các quyền đã tồn tại trong hệ thống.
     *
     * @return danh sách PermissionResponse tương ứng với mỗi quyền
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponse> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    /**
     * Xóa một quyền dựa trên mã quyền.
     *
     * @param permission mã quyền cần xóa
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePermission(String permission) {
        permissionRepository.deleteById(permission);
    }
}

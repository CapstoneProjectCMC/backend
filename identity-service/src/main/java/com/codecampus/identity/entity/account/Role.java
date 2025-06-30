package com.codecampus.identity.entity.account;

import com.codecampus.identity.entity.audit.AuditMetadata;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Role extends AuditMetadata
{
  @Id
  String name;

  String description;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "role_permissions",
      joinColumns = @JoinColumn(name = "role_name"),
      inverseJoinColumns = @JoinColumn(name = "permission_name"))
  Set<Permission> permissions = new HashSet<>();
}

using Microsoft.EntityFrameworkCore.ChangeTracking;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using OrganizationService.Core.Enums;
using OrganizationService.Core.Exceptions;
using OrganizationService.DataAccess.Models;
using System.Reflection.Emit;

namespace OrganizationService.DataAccess.DbContexts
{
    public class OrganizationServiceDbContext : IdentityDbContext
    {
        public OrganizationServiceDbContext(DbContextOptions<OrganizationServiceDbContext> options) : base(options)
        {
        }

        public virtual DbSet<Organization> Organizations { get; set; }
        public virtual DbSet<OrganizationMember> OrganizationMembers { get; set; }
        public virtual DbSet<Grade> Grades { get; set; }
        public virtual DbSet<Class> Classes { get; set; }
        public virtual DbSet<StudentCredential> StudentCredentials { get; set; }

        protected override void OnModelCreating(ModelBuilder builder)
        {
            base.OnModelCreating(builder);

            builder.Entity<Organization>().ToTable("Organizations");
            builder.Entity<OrganizationMember>().ToTable("OrganizationMembers");
            builder.Entity<Grade>().ToTable("Grades");
            builder.Entity<Class>().ToTable("Classes");
            builder.Entity<StudentCredential>().ToTable("StudentCredentials");
            builder.Entity<AuditChange>();

            //class Organization
            builder.Entity<Organization>()
                .HasMany(o => o.Grades)
                .WithOne(m => m.Organization)
                .HasForeignKey(m => m.OrganizationId)
                .OnDelete(DeleteBehavior.Cascade);

            builder.Entity<Organization>()
                .HasMany(o => o.OrganizationMembers)
                .WithOne()
                .HasForeignKey(om => om.ScopeId)
                .OnDelete(DeleteBehavior.NoAction);

            builder.Entity<Organization>()
               .HasMany(o => o.StudentCredentials)
               .WithOne(sc => sc.Organization)
               .HasForeignKey(sc => sc.OrganizationId)
               .OnDelete(DeleteBehavior.NoAction);


            // Grade
            builder.Entity<Grade>()
                .HasMany(g => g.Classes)
                .WithOne(c => c.Grade)
                .HasForeignKey(c => c.GradeId)
                .OnDelete(DeleteBehavior.Cascade);


            // Class
            builder.Entity<Class>()
                .HasMany(c => c.StudentCredentials)
                .WithOne(sc => sc.Class)
                .HasForeignKey(sc => sc.ClassId)
                .OnDelete(DeleteBehavior.Cascade);

            // OrganizationMember (Fluent config)
            builder.Entity<OrganizationMember>(entity =>
            {
                entity.HasIndex(e => new { e.UserId, e.ScopeType, e.ScopeId }).IsUnique();

                entity.Property(e => e.Role)
                    .HasConversion<string>()
                    .IsRequired();

                entity.Property(e => e.ScopeType)
                    .HasConversion<string>()
                    .IsRequired();

                entity.Property(e => e.IsActive)
                    .IsRequired();

            });



        }

        public override async Task<int> SaveChangesAsync(CancellationToken cancellationToken = default)
        {
            try
            {
                int rowCount;
                rowCount = await base.SaveChangesAsync(cancellationToken);

                await base.SaveChangesAsync();
                return rowCount;
            }
            catch (DbUpdateConcurrencyException ex)
            {
                throw new ErrorException(StatusCodeEnum.ConcurrencyConflict);
            }
        }
    }

    class AuditChangeMapping
    {
        public AuditChange AuditChange { get; set; }
        public BaseTable<Guid> DbRecord { get; set; }
        public BaseTable<Guid> GuidTableRecord { get; set; }
    }
}


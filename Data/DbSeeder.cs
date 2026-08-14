using Microsoft.EntityFrameworkCore;
using PackagingPortal.Api.Models;

namespace PackagingPortal.Api.Data;

public static class DbSeeder
{
    public static async Task SeedAsync(AppDbContext context)
    {
        if (!await context.AdminUsers.AnyAsync())
        {
            var admin = new AdminUser
            {
                Username = "admin",
                PasswordHash = BCrypt.Net.BCrypt.HashPassword("Admin@123"),
                CreatedAt = DateTime.UtcNow
            };
            context.AdminUsers.Add(admin);
            await context.SaveChangesAsync();
        }
    }
}
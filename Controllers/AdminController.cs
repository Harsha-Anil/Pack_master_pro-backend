using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PackagingPortal.Api.Data;
using PackagingPortal.Api.DTOs;
using PackagingPortal.Api.Models;

namespace PackagingPortal.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
[Authorize(Roles = "Admin")]
public class AdminController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly ILogger<AdminController> _logger;

    public AdminController(AppDbContext context, ILogger<AdminController> logger)
    {
        _context = context;
        _logger = logger;
    }

    // (only existing admins can do this)
    [HttpPost("users")]
    public async Task<IActionResult> CreateUser([FromBody] CreateAdminDto dto)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        var exists = await _context.AdminUsers.AnyAsync(u => u.Username == dto.Username);
        if (exists)
            return Conflict(new { message = "Username already exists" });

        var user = new AdminUser
        {
            Username = dto.Username,
            PasswordHash = BCrypt.Net.BCrypt.HashPassword(dto.Password),
            CreatedAt = DateTime.UtcNow
        };

        _context.AdminUsers.Add(user);
        await _context.SaveChangesAsync();

        _logger.LogInformation("New admin user created: {Username}", user.Username);

        return CreatedAtAction(nameof(GetUsers), new { id = user.Id, username = user.Username });
    }

    // GET /api/admin/users — list all admin users
    [HttpGet("users")]
    public async Task<IActionResult> GetUsers()
    {
        var users = await _context.AdminUsers
            .Select(u => new { u.Id, u.Username, u.CreatedAt })
            .ToListAsync();

        return Ok(users);
    }
}
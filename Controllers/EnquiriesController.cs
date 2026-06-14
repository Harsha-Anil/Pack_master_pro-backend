using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PackagingPortal.Api.Data;
using PackagingPortal.Api.DTOs;
using PackagingPortal.Api.Models;

namespace PackagingPortal.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class EnquiriesController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly ILogger<EnquiriesController> _logger;

    public EnquiriesController(AppDbContext context, ILogger<EnquiriesController> logger)
    {
        _context = context;
        _logger = logger;
    }

    // POST /api/enquiries
    [HttpPost]
    public async Task<ActionResult<EnquiryResponseDto>> Create([FromBody] CreateEnquiryDto dto)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        var enquiry = new Enquiry
        {
            Name = dto.Name,
            CompanyName = dto.CompanyName,
            PhoneNumber = dto.PhoneNumber,
            Email = dto.Email,
            Subject = dto.Subject,
            Message = dto.Message,
            Status = "NEW",
            CreatedAt = DateTime.UtcNow
        };

        _context.Enquiries.Add(enquiry);
        await _context.SaveChangesAsync();

        _logger.LogInformation("New enquiry #{Id} from {Name} ({Email})", enquiry.Id, enquiry.Name, enquiry.Email);

        var response = MapToResponse(enquiry);
        return CreatedAtAction(nameof(GetById), new { id = enquiry.Id }, response);
    }

    // GET /api/enquiries
    [HttpGet]
    public async Task<ActionResult<IEnumerable<EnquiryResponseDto>>> GetAll(
        [FromQuery] string? status,
        [FromQuery] string? search,
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 20)
    {
        var query = _context.Enquiries.AsQueryable();

        if (!string.IsNullOrWhiteSpace(status))
            query = query.Where(e => e.Status == status);

        if (!string.IsNullOrWhiteSpace(search))
            query = query.Where(e => e.Name.Contains(search) || (e.CompanyName != null && e.CompanyName.Contains(search)) || e.Email.Contains(search));

        var total = await query.CountAsync();

        var enquiries = await query
            .OrderByDescending(e => e.CreatedAt)
            .Skip((page - 1) * pageSize)
            .Take(pageSize)
            .Select(e => MapToResponse(e))
            .ToListAsync();

        Response.Headers.Append("X-Total-Count", total.ToString());
        return Ok(enquiries);
    }

    // GET /api/enquiries/{id}
    [HttpGet("{id}")]
    public async Task<ActionResult<EnquiryResponseDto>> GetById(int id)
    {
        var enquiry = await _context.Enquiries.FindAsync(id);
        if (enquiry == null)
            return NotFound();

        return Ok(MapToResponse(enquiry));
    }

    // PUT /api/enquiries/{id}/status
    [HttpPut("{id}/status")]
    public async Task<ActionResult<EnquiryResponseDto>> UpdateStatus(int id, [FromBody] UpdateStatusDto dto)
    {
        var enquiry = await _context.Enquiries.FindAsync(id);
        if (enquiry == null)
            return NotFound();

        enquiry.Status = dto.Status;
        await _context.SaveChangesAsync();

        return Ok(MapToResponse(enquiry));
    }

    // PUT /api/enquiries/{id}/notes
    [HttpPut("{id}/notes")]
    public async Task<ActionResult<EnquiryResponseDto>> UpdateNotes(int id, [FromBody] UpdateNotesDto dto)
    {
        var enquiry = await _context.Enquiries.FindAsync(id);
        if (enquiry == null)
            return NotFound();

        enquiry.Notes = dto.Notes;
        await _context.SaveChangesAsync();

        return Ok(MapToResponse(enquiry));
    }

    private static EnquiryResponseDto MapToResponse(Enquiry e) => new()
    {
        Id = e.Id,
        Name = e.Name,
        CompanyName = e.CompanyName,
        PhoneNumber = e.PhoneNumber,
        Email = e.Email,
        Subject = e.Subject,
        Message = e.Message,
        Status = e.Status,
        Notes = e.Notes,
        CreatedAt = e.CreatedAt
    };
}
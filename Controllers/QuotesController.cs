using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PackagingPortal.Api.Data;
using PackagingPortal.Api.DTOs;
using PackagingPortal.Api.Models;

namespace PackagingPortal.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class QuotesController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly ILogger<QuotesController> _logger;
    private readonly IWebHostEnvironment _env;

    public QuotesController(AppDbContext context, ILogger<QuotesController> logger, IWebHostEnvironment env)
    {
        _context = context;
        _logger = logger;
        _env = env;
    }

    // POST /api/quotes
    [HttpPost]
    public async Task<ActionResult<QuoteRequestResponseDto>> Create([FromForm] CreateQuoteRequestDto dto, IFormFile? referenceImage, IFormFile? artwork)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        var quote = new QuoteRequest
        {
            Name = dto.Name,
            CompanyName = dto.CompanyName,
            PhoneNumber = dto.PhoneNumber,
            Email = dto.Email,
            ProductType = dto.ProductType,
            Length = dto.Length,
            Width = dto.Width,
            Height = dto.Height,
            Quantity = dto.Quantity,
            Color = dto.Color,
            PrintingRequired = dto.PrintingRequired,
            DeliveryLocation = dto.DeliveryLocation,
            ExpectedTimeline = dto.ExpectedTimeline,
            AdditionalNotes = dto.AdditionalNotes,
            Status = "NEW",
            CreatedAt = DateTime.UtcNow
        };

        // Handle file uploads
        if (referenceImage != null && referenceImage.Length > 0)
            quote.ReferenceImagePath = await SaveFileAsync(referenceImage, "reference-images");

        if (artwork != null && artwork.Length > 0)
            quote.ArtworkPath = await SaveFileAsync(artwork, "artworks");

        _context.QuoteRequests.Add(quote);
        await _context.SaveChangesAsync();

        _logger.LogInformation("New quote request #{Id} from {Name} ({Email}) for {Product}", quote.Id, quote.Name, quote.Email, quote.ProductType);

        var response = MapToResponse(quote);
        return CreatedAtAction(nameof(GetById), new { id = quote.Id }, response);
    }

    // GET /api/quotes
    [HttpGet]
    public async Task<ActionResult<IEnumerable<QuoteRequestResponseDto>>> GetAll(
        [FromQuery] string? status,
        [FromQuery] string? productType,
        [FromQuery] string? search,
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 20)
    {
        var query = _context.QuoteRequests.AsQueryable();

        if (!string.IsNullOrWhiteSpace(status))
            query = query.Where(q => q.Status == status);
        if (!string.IsNullOrWhiteSpace(productType))
            query = query.Where(q => q.ProductType == productType);
        if (!string.IsNullOrWhiteSpace(search))
            query = query.Where(q => q.Name.Contains(search) || (q.CompanyName != null && q.CompanyName.Contains(search)) || q.Email.Contains(search));

        var total = await query.CountAsync();

        var quotes = await query
            .OrderByDescending(q => q.CreatedAt)
            .Skip((page - 1) * pageSize)
            .Take(pageSize)
            .Select(q => MapToResponse(q))
            .ToListAsync();

        Response.Headers.Append("X-Total-Count", total.ToString());
        return Ok(quotes);
    }

    // GET /api/quotes/{id}
    [HttpGet("{id}")]
    public async Task<ActionResult<QuoteRequestResponseDto>> GetById(int id)
    {
        var quote = await _context.QuoteRequests.FindAsync(id);
        if (quote == null)
            return NotFound();

        return Ok(MapToResponse(quote));
    }

    // PUT /api/quotes/{id}/status
    [HttpPut("{id}/status")]
    public async Task<ActionResult<QuoteRequestResponseDto>> UpdateStatus(int id, [FromBody] UpdateStatusDto dto)
    {
        var quote = await _context.QuoteRequests.FindAsync(id);
        if (quote == null)
            return NotFound();

        quote.Status = dto.Status;
        await _context.SaveChangesAsync();

        return Ok(MapToResponse(quote));
    }

    // PUT /api/quotes/{id}/notes
    [HttpPut("{id}/notes")]
    public async Task<ActionResult<QuoteRequestResponseDto>> UpdateNotes(int id, [FromBody] UpdateNotesDto dto)
    {
        var quote = await _context.QuoteRequests.FindAsync(id);
        if (quote == null)
            return NotFound();

        quote.Notes = dto.Notes;
        await _context.SaveChangesAsync();

        return Ok(MapToResponse(quote));
    }

    private async Task<string> SaveFileAsync(IFormFile file, string folder)
    {
        var uploadsDir = Path.Combine(_env.ContentRootPath, "Uploads", folder);
        Directory.CreateDirectory(uploadsDir);

        var uniqueName = $"{Guid.NewGuid()}_{Path.GetFileName(file.FileName)}";
        var filePath = Path.Combine(uploadsDir, uniqueName);

        using var stream = new FileStream(filePath, FileMode.Create);
        await file.CopyToAsync(stream);

        return $"/uploads/{folder}/{uniqueName}";
    }

    private static QuoteRequestResponseDto MapToResponse(QuoteRequest q) => new()
    {
        Id = q.Id,
        Name = q.Name,
        CompanyName = q.CompanyName,
        PhoneNumber = q.PhoneNumber,
        Email = q.Email,
        ProductType = q.ProductType,
        Length = q.Length,
        Width = q.Width,
        Height = q.Height,
        Quantity = q.Quantity,
        Color = q.Color,
        PrintingRequired = q.PrintingRequired,
        DeliveryLocation = q.DeliveryLocation,
        ExpectedTimeline = q.ExpectedTimeline,
        AdditionalNotes = q.AdditionalNotes,
        ReferenceImagePath = q.ReferenceImagePath,
        ArtworkPath = q.ArtworkPath,
        Status = q.Status,
        Notes = q.Notes,
        CreatedAt = q.CreatedAt
    };
}
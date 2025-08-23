
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using OrganizationService.Api.Middlewares;
using OrganizationService.Api.Utils;
using OrganizationService.Core.ApiModels;
using OrganizationService.DataAccess.DbContexts;
using OrganizationService.DataAccess.Implementation;
using OrganizationService.DataAccess.Interfaces;
using OrganizationService.Service.Implementation;
using OrganizationService.Service.Interfaces;
using System.Data;
using System.Text;
using System.Text.Json.Serialization;
using Confluent.Kafka;
using OrganizationService.Core.Constants;

var builder = WebApplication.CreateBuilder(args);
//builder.Logging.AddAzureWebAppDiagnostics();

var config = builder.Configuration;

var MyAllowSpecificOrigins = "_myAllowSpecificOrigins";

// Add services to the container.
var appSettingsSection = builder.Configuration.GetSection("AppSettings");
builder.Services.Configure<AppSettings>(appSettingsSection);
var appSettings = appSettingsSection?.Get<AppSettings>();
builder.Services.AddSingleton(appSettings ?? new AppSettings());

builder.Services.AddControllers().AddJsonOptions(x => x.JsonSerializerOptions.ReferenceHandler = ReferenceHandler.IgnoreCycles);

builder.Services.AddHttpContextAccessor();

builder.Services.AddDbContext<OrganizationServiceDbContext>(options => options.UseSqlServer(builder.Configuration.GetConnectionString("Default")));

builder.Services.AddScoped(typeof(IRepository<>), typeof(Repository<>));
builder.Services.AddScoped<IUnitOfWork, UnitOfWork>();
builder.Services.AddScoped<UserContext>();
builder.Services.AddScoped<IStudentCredentialService, StudentCredentialService>();
builder.Services.AddScoped<IClassService, ClassService>();
builder.Services.AddScoped<IGradeService, GradeService>();
builder.Services.AddScoped<IOrganizationService, OrganizationService.Service.Implementation.OrganizationService>();
builder.Services.AddScoped<IOrganizationMemberService, OrganizationMemberService>();
builder.Services.AddScoped<IOrgEventPublisher, OrgEventPublisher>();
builder.Services.AddScoped<IOrgMemberEventPublisher, OrgMemberEventPublisher>();


builder.Services.Configure<IdentityOptions>(options =>
{
    options.Password.RequireDigit = true;
    options.Password.RequireLowercase = true;
    options.Password.RequireNonAlphanumeric = true;
    options.Password.RequireUppercase = true;
    options.Password.RequiredLength = 8;
    options.Password.RequiredUniqueChars = 1;
});

builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultScheme = JwtBearerDefaults.AuthenticationScheme;
}).AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidateAudience = true,
        ValidateLifetime = true,
        ValidateIssuerSigningKey = true,
        ValidIssuer = appSettings.Jwt.Issuer,
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(appSettings.Jwt.Key))
    };
});

// Thêm MemoryCache để cache claims
builder.Services.AddMemoryCache();

//add author policies
builder.Services.AddAuthorization(options =>
{
    options.AddPolicy("AdminOnly", policy => policy.RequireRole("ADMIN"));
    options.AddPolicy("TeacherOrAdmin", policy => policy.RequireRole("TEACHER", "SYS_ADMIN"));
    options.AddPolicy("SysAdminOnly", policy => policy.RequireRole("SYS_ADMIN"));
    options.AddPolicy("OrgAdminOnly", policy => policy.RequireRole("ORG_ADMIN"));
    options.AddPolicy("TeacherOnly", policy => policy.RequireRole("TEACHER"));
    options.AddPolicy("UserOnly", policy => policy.RequireRole("USER"));
    options.AddPolicy("LoggedInUsers", policy => policy.RequireRole("USER", "TEACHER", "ORG_ADMIN", "SYS_ADMIN"));
});

builder.Services.AddCors(options =>
{
    options.AddPolicy(name: MyAllowSpecificOrigins,
                      policy =>
                      {
                          policy.WithOrigins("*")
                          .AllowAnyHeader()
                          .AllowAnyMethod();
                      });
});


//builder.Services.AddTwilioClient();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

//builder.Services.AddAutoMapper(AppDomain.CurrentDomain.GetAssemblies());

builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo { Title = "OrganizationService API", Version = "v1", Description = $"Last updated at {DateTimeOffset.UtcNow}" });
    c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Description = @"JWT Authorization header using the Bearer scheme.
                      Enter 'Bearer' [space] and then your token in the text input below.
                      Example: 'Bearer 12345abcdef'",
        Name = "Authorization",
        In = ParameterLocation.Header,
        Type = SecuritySchemeType.ApiKey,
        Scheme = "Bearer"
    });
    c.AddSecurityRequirement(new OpenApiSecurityRequirement()
                {
                    {
                        new OpenApiSecurityScheme
                        {
                        Reference = new OpenApiReference
                            {
                            Type = ReferenceType.SecurityScheme,
                            Id = "Bearer"
                            },
                            Scheme = "oauth2",
                            Name = "Bearer",
                            In = ParameterLocation.Header,

                        },
                        new List<string>()
        }
    });
});

builder.WebHost.ConfigureKestrel(options =>
{
    options.Limits.MaxRequestBodySize = 6L * 1024 * 1024 * 1024; // 6GB
        BootstrapServers = cfg.BootstrapServers,
        Acks = Acks.All,
        EnableIdempotence = true,
        LingerMs = 5,
        BatchSize = 32 * 1024
    }).Build();
});

builder.Services.AddSingleton<KafkaOptions>(builder.Configuration.GetSection("Kafka").Get<KafkaOptions>() ?? new());
builder.Services.AddScoped<IOrgEventPublisher, OrgEventPublisher>();
builder.Services.AddScoped<IOrgMemberEventPublisher, OrgMemberEventPublisher>();

var app = builder.Build();

app.MigrateDatabase();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UsePathBase("/org");

app.UseCors(MyAllowSpecificOrigins);

app.UseHttpsRedirection();
app.UseAuthentication();
app.UseAuthorization();

app.UseMiddleware<ExceptionMiddleware>();
app.UseMiddleware<AuthenMiddleware>();
app.UseMiddleware<UserContextMiddleware>();

app.MapControllers();

app.Run();

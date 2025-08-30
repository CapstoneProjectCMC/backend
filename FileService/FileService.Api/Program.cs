using FileService.Api.Middlewares;
using FileService.Api.Utils;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using FileService.Core.ApiModels;
using Microsoft.AspNetCore.Identity;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using System.Data;
using System.Text;
using System.Text.Json.Serialization;
using FileService.DataAccess.Interfaces;
using FileService.DataAccess.Implementation;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Serializers;
using MongoDB.Bson.Serialization;
using FileService.Service.Implementation;
using FileService.Service.Interfaces;
using Microsoft.Extensions.FileProviders;
using System.Security.Claims;

BsonSerializer.RegisterSerializer(new GuidSerializer(GuidRepresentation.Standard));

var builder = WebApplication.CreateBuilder(args);

//env trong docker override fle json
builder.Configuration.AddEnvironmentVariables();

var config = builder.Configuration;

//var MyAllowSpecificOrigins = "_myAllowSpecificOrigins";

// Add services to the container.
var appSettingsSection = builder.Configuration.GetSection("AppSettings");
builder.Services.Configure<AppSettings>(appSettingsSection);
var appSettings = appSettingsSection?.Get<AppSettings>();
builder.Services.AddSingleton(appSettings ?? new AppSettings());

builder.Services.AddControllers().AddJsonOptions(x => x.JsonSerializerOptions.ReferenceHandler = ReferenceHandler.IgnoreCycles);

builder.Services.AddHttpContextAccessor();

// Add HttpClient factory
builder.Services.AddHttpClient<IIdentityServiceClient, IdentityServiceClient>();

var env = Environment.GetEnvironmentVariable("ASPNETCORE_ENVIRONMENT");
Console.WriteLine($"ASPNETCORE_ENVIRONMENT: {env}");

builder.Services.Configure<MongoDbSettings>(builder.Configuration.GetSection("MongoDbSettings"));
builder.Services.Configure<FfmpegSettings>(builder.Configuration.GetSection("FfmpegSettings"));
builder.Services.Configure<MinioConfig>(builder.Configuration.GetSection("AppSettings:MinioConfig"));


// Đăng ký MongoClient và Database
builder.Services.AddSingleton<IMongoClient>(sp =>
{
    var settings = sp.GetRequiredService<IOptions<MongoDbSettings>>().Value;

    var mongoUrl = new MongoUrl(settings.ConnectionStrings);
    var clientSettings = MongoClientSettings.FromUrl(mongoUrl);

    return new MongoClient(clientSettings);
});

builder.Services.AddSingleton<IMongoDatabase>(sp =>
{
    var settings = sp.GetRequiredService<IOptions<MongoDbSettings>>().Value;
    var client = sp.GetRequiredService<IMongoClient>();
    return client.GetDatabase(settings.DatabaseName);
});


builder.Services.AddScoped(typeof(IRepository<>), typeof(Repository<>));
builder.Services.AddScoped<UserContext>();
builder.Services.AddScoped<IFileDocumentService, FileDocumentService>();
builder.Services.AddScoped<IFfmpegService, FfmpegService>();
builder.Services.AddScoped<IMinioService, MinioService>();
builder.Services.AddScoped<ITagService, TagService>();
builder.Services.AddScoped<IIdentityServiceClient, IdentityServiceClient>();


builder.Services.Configure<IdentityOptions>(options =>
{
    options.Password.RequireDigit = true;
    options.Password.RequireLowercase = true;
    options.Password.RequireNonAlphanumeric = true;
    options.Password.RequireUppercase = true;
    options.Password.RequiredLength = 8;
    options.Password.RequiredUniqueChars = 1;
});


//add authen
builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultScheme = JwtBearerDefaults.AuthenticationScheme;
}).AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = false,
        ValidateAudience = false,
        ValidateLifetime = false,
        ValidateIssuerSigningKey = true,
        ValidIssuer = appSettings.Jwt.Issuer,
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(appSettings.Jwt.Key)),

        // Cấu hình claim để nhận Role
        RoleClaimType = ClaimTypes.Role,
        NameClaimType = ClaimTypes.NameIdentifier,
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

//builder.Services.AddCors(options =>
//{
//    options.AddPolicy(name: MyAllowSpecificOrigins,
//                      policy =>
//                      {
//                          policy.AllowAnyOrigin()
//                          .AllowAnyHeader()
//                          .AllowAnyMethod();
//                      });
//});


// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

//builder.Services.AddAutoMapper(AppDomain.CurrentDomain.GetAssemblies());

builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo { Title = "CodeCampus API", Version = "v1", Description = $"Last updated at {DateTimeOffset.UtcNow}" });
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
        }
    );
});

builder.WebHost.ConfigureKestrel(options =>
{
    options.Limits.MaxRequestBodySize = 6L * 1024 * 1024 * 1024; // 6GB
});


var app = builder.Build();

// Migrate MongoDB and seed data if necessary
app.MigrateMongoDb();


// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

//kích hoạt CORS policy
//app.UseCors(MyAllowSpecificOrigins);

app.UseHttpsRedirection();
app.UseAuthentication();
app.UseAuthorization();

app.UseMiddleware<ExceptionMiddleware>();
app.UseMiddleware<AuthenMiddleware>();
app.UseMiddleware<UserContextMiddleware>();

app.MapControllers();

app.Run();


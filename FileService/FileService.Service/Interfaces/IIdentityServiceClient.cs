using FileService.Service.ApiModels.UserModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Service.Interfaces
{
    public interface IIdentityServiceClient
    {
        Task<UserProfileResponse?> GetUserProfileAsync(string userId);
        Task<List<UserProfileResponse>> GetAllUserProfilesAsync(int page, int size);
    }
}

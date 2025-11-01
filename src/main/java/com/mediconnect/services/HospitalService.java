package com.mediconnect.services;

import com.mediconnect.dtos.HospitalResponseDTO;
import com.mediconnect.model.Hospital;
import com.mediconnect.repositories.HospitalRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HospitalService {
    @Autowired
    private ImageUploadService imageUploadService;
    @Autowired
    private HospitalRepository hospitalRepository;

    public HospitalResponseDTO createHospital(Hospital hospital, List<MultipartFile> images, String adminId){
        if (hospitalRepository.existsByRegistrationNumber(hospital.getRegistrationNumber())) {
            throw new RuntimeException("Hospital with this registration number already exists");
        }
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            String folderName = "hospitals/" + adminId + "/" +
                    (hospital.getRegistrationNumber() != null ? hospital.getRegistrationNumber() : "unknown");

            for (MultipartFile file : images) {
                try {
                    String imageUrl = imageUploadService.uploadImage(file, folderName);
                    imageUrls.add(imageUrl);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to upload image: " + e.getMessage());
                }
            }
        }
        hospital.setImages(imageUrls);
        hospital.setAdminId(adminId);
        hospital.setActive(true);
        hospital.setCreatedAt(System.currentTimeMillis());
        hospital.setUpdatedAt(System.currentTimeMillis());
        Hospital savedHospital = hospitalRepository.save(hospital);
        return convertToDTO(savedHospital);

    }
    // get Hospital by Admin
    public HospitalResponseDTO getHospitalByAdmin(String adminId){

        Hospital hospital = hospitalRepository.findByAdminId(adminId)
                .orElseThrow(()-> new RuntimeException("No Hospital Found for this Admin"));
        return convertToDTO(hospital);
    }
    // delete hospital
    public void deleteHospitalById(String hospitalId, String adminId){
        ObjectId hospitalObjectId = new ObjectId(hospitalId);
        ObjectId adminObjectId = new ObjectId(adminId);
        Hospital hospital = hospitalRepository.findById(hospitalObjectId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
        if (!hospital.getAdminId().equals(adminId)) {
            throw new RuntimeException("Unauthorized: You cannot delete another admin's hospital");
        }
        // delete images from cloudinary
        if(hospital.getImages() != null && !hospital.getImages().isEmpty()){
            for (String imageUrl:hospital.getImages()){
                try{
    imageUploadService.deleteImage(imageUrl);
                }catch (Exception e){
                    System.err.println("Error deleting image from Cloudinary: " + e.getMessage());
                }
            }
        }
        hospitalRepository.delete(hospital);

    }
    // update hospital
    public HospitalResponseDTO updateHospital(String hospitalId,Hospital updatedHospital,List<MultipartFile> newImages,String adminId){
        ObjectId hospitalObjectId = new ObjectId(hospitalId);
        Hospital existingHospital = hospitalRepository.findById(hospitalObjectId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
        // Verify admin
        if (!existingHospital.getAdminId().equals(adminId)) {
            throw new RuntimeException("Unauthorized: Cannot update another admin's hospital");
        }

        // Images handling
        if(newImages!=null && !newImages.isEmpty()){
            // delete old images from cloudinary
            if(existingHospital.getImages()!=null){
                for (String oldImageUrl: existingHospital.getImages()){
                    try {
                        imageUploadService.deleteImage(oldImageUrl);
                    } catch (Exception e) {
                        System.err.println("Error deleting old image: " + e.getMessage());
                    }
                }
            }
            // upload new images
            List<String> uploadedUrls = new ArrayList<>();
            String folderName = "hospitals/" + adminId + "/" +
                    (existingHospital.getRegistrationNumber() != null ? existingHospital.getRegistrationNumber() : "unknown");
            for (MultipartFile file:newImages){
                try{
                  String imageUrl = imageUploadService.uploadImage(file,folderName);
                  uploadedUrls.add(imageUrl);
                }catch (Exception e){
                    throw new RuntimeException("Failed to upload image: " + e.getMessage());
                }
            }
            existingHospital.setImages(uploadedUrls);
        }
        if (updatedHospital.getName() != null) existingHospital.setName(updatedHospital.getName());
        if (updatedHospital.getWebsite() != null) existingHospital.setWebsite(updatedHospital.getWebsite());
        if (updatedHospital.getPhoneNumber() != null) existingHospital.setPhoneNumber(updatedHospital.getPhoneNumber());
        if (updatedHospital.getAddressLine1() != null) existingHospital.setAddressLine1(updatedHospital.getAddressLine1());
        if (updatedHospital.getAddressLine2() != null) existingHospital.setAddressLine2(updatedHospital.getAddressLine2());
        if (updatedHospital.getCity() != null) existingHospital.setCity(updatedHospital.getCity());
        if (updatedHospital.getState() != null) existingHospital.setState(updatedHospital.getState());
        if (updatedHospital.getCountry() != null) existingHospital.setCountry(updatedHospital.getCountry());
        if (updatedHospital.getZipCode() != null) existingHospital.setZipCode(updatedHospital.getZipCode());
        if (updatedHospital.getDepartments() != null) existingHospital.setDepartments(updatedHospital.getDepartments());
        if (updatedHospital.getServices() != null) existingHospital.setServices(updatedHospital.getServices());
        if (updatedHospital.getFacilities() != null) existingHospital.setFacilities(updatedHospital.getFacilities());
        existingHospital.setEmergencyAvailable(updatedHospital.isEmergencyAvailable());
        existingHospital.setTotalBeds(updatedHospital.getTotalBeds());
        existingHospital.setAvailableBeds(updatedHospital.getAvailableBeds());
        existingHospital.setOpen24Hours(updatedHospital.isOpen24Hours());
        if (updatedHospital.getOpeningTime() != null) existingHospital.setOpeningTime(updatedHospital.getOpeningTime());
        if (updatedHospital.getClosingTime() != null) existingHospital.setClosingTime(updatedHospital.getClosingTime());
        if (updatedHospital.getUpiId() != null) existingHospital.setUpiId(updatedHospital.getUpiId());

        existingHospital.setUpdatedAt(System.currentTimeMillis());
        Hospital savedHospital = hospitalRepository.save(existingHospital);
        return convertToDTO(savedHospital);

    }

    public HospitalResponseDTO convertToDTO(Hospital hospital) {
        return HospitalResponseDTO.builder()
                .id(hospital.getId().toString())
                .name(hospital.getName())
                .registrationNumber(hospital.getRegistrationNumber())
                .website(hospital.getWebsite())
                .email(hospital.getEmail())
                .phoneNumber(hospital.getPhoneNumber())
                .addressLine1(hospital.getAddressLine1())
                .addressLine2(hospital.getAddressLine2())
                .city(hospital.getCity())
                .state(hospital.getState())
                .country(hospital.getCountry())
                .zipCode(hospital.getZipCode())
                .departments(hospital.getDepartments())
                .services(hospital.getServices())
                .facilities(hospital.getFacilities())
                .emergencyAvailable(hospital.isEmergencyAvailable())
                .totalBeds(hospital.getTotalBeds())
                .availableBeds(hospital.getAvailableBeds())
                .open24Hours(hospital.isOpen24Hours())
                .openingTime(hospital.getOpeningTime())
                .closingTime(hospital.getClosingTime())
                .images(hospital.getImages())
                .rating(hospital.getRating())
                .upiId(hospital.getUpiId())
                .build();
    }

    // ---------------- Fetch top 10 hospitals ----------------
    public List<HospitalResponseDTO> getTopHospitals() {
        List<Hospital> hospitals = hospitalRepository.findTop10ByOrderByRatingDesc();
        return hospitals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ---------------- Fetch all hospitals ----------------
    public List<HospitalResponseDTO> getAllHospitals() {
        List<Hospital> hospitals = hospitalRepository.findAll();
        return hospitals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public HospitalResponseDTO authenticateHospital(String adminId,String email, String regNo) {

        Hospital hospital = hospitalRepository.findByEmailAndRegistrationNumber(email, regNo)
                .orElseThrow(() -> new RuntimeException("Hospital email or registration number invalid"));

        if(!hospital.getAdminId().equals(adminId)){
            throw new RuntimeException("Unauthorized: Hospital does not belong to this admin");
        }
        return convertToDTO(hospital);
    }

}

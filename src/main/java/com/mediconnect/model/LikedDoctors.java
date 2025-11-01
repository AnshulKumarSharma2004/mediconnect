package com.mediconnect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "liked_doctors")
public class LikedDoctors {

    @Id
    private ObjectId id;

    private String userId;
    private List<String> doctorIds = new ArrayList<>();




}

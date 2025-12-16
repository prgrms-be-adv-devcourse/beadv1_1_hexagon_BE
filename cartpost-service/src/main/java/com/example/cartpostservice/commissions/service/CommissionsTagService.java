package com.example.cartpostservice.commissions.service;

import com.example.cartpostservice.commissions.model.CommissionsTagEntity;
import com.example.cartpostservice.commissions.repository.CommissionsTagRepository;
import com.example.cartpostservice.commissions.service.usecase.command.TagServiceCommand;
import com.example.cartpostservice.commissions.service.usecase.result.TagServiceResult;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionsTagService implements CrudService<TagServiceCommand, TagServiceResult, String> {

    private final CommissionsTagRepository commissionsTagRepository;

    @Override
    public String create(TagServiceCommand requestDto) {
        for (String tagCode : requestDto.tagCodes()) {
            CommissionsTagEntity commissionsTagEntity = CommissionsTagEntity.builder()
                    .commissionCode(requestDto.commissionsCode())
                    .tagCode(tagCode).build();

            commissionsTagRepository.save(commissionsTagEntity);
        }

        return requestDto.commissionsCode();
    }

    @Override
    public TagServiceResult read(String commissionCode) {
        List<CommissionsTagEntity> tags = commissionsTagRepository.findByCommissionCode(commissionCode);

        List<String> tagCodes = tags.stream()
                .map(CommissionsTagEntity::getTagCode)
                .toList();

        return new TagServiceResult(
                commissionCode,
                tagCodes
        );
    }

    @Override
    public void update(TagServiceCommand requestDto, String commissionCode) {
        List<CommissionsTagEntity> savedTags = commissionsTagRepository.findByCommissionCode(commissionCode);

        Set<String> requestedTagCodes = new HashSet<>(requestDto.tagCodes());

        List<CommissionsTagEntity> toDeleteTags = savedTags.stream()
                .filter(tag -> !requestedTagCodes.contains(tag.getTagCode()))
                .toList();

        Set<String> existingCodes = savedTags.stream()
                .map(CommissionsTagEntity::getTagCode)
                .collect(Collectors.toSet());

        List<CommissionsTagEntity> toSaveTags = requestDto.tagCodes().stream()
                .filter(tagCode -> !existingCodes.contains(tagCode))
                .map(tagCode -> CommissionsTagEntity.builder()
                        .commissionCode(commissionCode)
                        .tagCode(tagCode)
                        .build())
                .toList();

        commissionsTagRepository.deleteAll(toDeleteTags);
        commissionsTagRepository.saveAll(toSaveTags);
    }

    @Override
    public void delete(String commissionCode, String tagCode) {
        commissionsTagRepository.deleteByCommissionCode(commissionCode);
    }

    public List<TagServiceResult> getTags(List<String> commissionCodes) {
        List<CommissionsTagEntity> tags = commissionsTagRepository.findAllByCommissionCodeIn(commissionCodes);

        return tags.stream()
                .collect(Collectors.groupingBy(
                        CommissionsTagEntity::getCommissionCode,
                        Collectors.mapping(
                                CommissionsTagEntity::getTagCode,
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                .map(entry -> new TagServiceResult(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}

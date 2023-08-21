package com.example.todolist.card.service;

import com.example.todolist.card.entity.Card;
import com.example.todolist.card.entity.Share;
import com.example.todolist.card.repository.CardRepository;
import com.example.todolist.card.repository.ShareRepository;
import com.example.todolist.common.exception.CustomException;
import com.example.todolist.common.exception.ErrorCode;
import com.example.todolist.member.entity.Member;
import com.example.todolist.member.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareRepository shareRepository;
    private final CardRepository cardRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public void addShare(Member member, Long cardNo, List<Long> friends){
        Card card = cardRepository.findById(cardNo).orElseThrow(
                ()->new CustomException(ErrorCode.CARD_READ_FAILED)
        );

        if(!card.getMember().getMemberNo().equals(member.getMemberNo()))
            throw new CustomException(ErrorCode.AUTH_FAIL);

        friends.forEach(friend -> {

            shareRepository.save(Share.builder()
                    .member(member)
                    .toShare(friend)
                    .done(false)
                    .card(card).build());
        });
    }

    @Transactional
    public void deleteShare(Member member, Long sharedNo) {
        Share share = shareRepository.findById(sharedNo).orElseThrow(
                ()->new CustomException(ErrorCode.CARD_READ_FAILED)
        );
        if(!share.getToShare().equals(member.getMemberNo())
                && !share.getCard().getMember().getMemberNo().equals(member.getMemberNo())){
          throw new CustomException(ErrorCode.AUTH_FAIL);
        }
        shareRepository.deleteById(sharedNo);
    }
}

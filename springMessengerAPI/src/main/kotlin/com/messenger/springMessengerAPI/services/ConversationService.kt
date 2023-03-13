package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.models.Conversation
import com.messenger.springMessengerAPI.models.FriendshipStatus
import com.messenger.springMessengerAPI.models.User
import com.messenger.springMessengerAPI.models.UserConversation
import com.messenger.springMessengerAPI.models.response.ConversationResponse
import com.messenger.springMessengerAPI.repositories.*
import org.springframework.stereotype.Service


@Service
class ConversationService(
    private val conversationRepository: ConversationRepository,
    private val userConversationRepository: UserConversationRepository,
    private val friendRepository: FriendRepository,
    private val messageRepository: MessageRepository
) {

    fun getConversationsForUser(userId: Int): List<ConversationResponse> {
        val conversations = userConversationRepository.findAllByUserId(userId).map { it.conversation!! }
        return conversations.map {
            ConversationResponse(
                id = it.id,
                conversationName = getConversationName(it, userId),
                lastUpdated = it.lastUpdated
            )
        }
    }

    fun getConversationNameSetValueFromId(conversationId: Int): String? {
        return conversationRepository.findById(conversationId).get().conversationName
    }

    //Conversation name will either be the name specified for a conversation
    //Or it will be a list of the participants
    fun getConversationName(conversation: Conversation, userId: Int): String {
        //return the conversationname if it exists
        if (!conversation.conversationName.isNullOrEmpty()) {
            return conversation.conversationName
        }

        //If conversation list has one member, its with yourself
        if (conversation.userConversation.size == 1) {
            return conversation.userConversation.first().user?.username ?: "Unknown conversation"
        }
        //Else the conversation would be with a group of participants
        //This works for group conversations and private p2p conversations
        else {
            var convoName = ""
            for (userConvo in conversation.userConversation) {
                userConvo.user?.let {
                    if (it.id != userId) {
                        convoName += it.username + ", "
                    }
                }
            }
            //Remove last ", "
            return convoName.dropLast(2)

        }
    }

    fun findAPrivateConversationForTwoUsers(userSelfId: Int, userOtherId: Int): Conversation?{
        return conversationRepository.selectAPrivateNonGroupConversationForTwoUsers(userSelfId, userOtherId)
    }

    /**
     * This method returns a conversation if it already exists for two users, or will create a conversation for the two users
     *
     * @param usersForConversationRequest - contains the ids of the two users that the conversation is for
     * @return a conversationResponse
     */
    fun getOrStartAPrivateConversationForTwoUsers(userSelf: User, userOther: User): Conversation? {

        //Edge case, users are equal
        if(userSelf.id == userOther.id){
            throw RuntimeException("UserIdSelf and OtherUserId cannot be equal")
        }
        //Check that the two users are friends
        if(friendRepository.findFriendBySelfUserIdAndFriendUseridAndStatus(userSelf.id, userOther.id, FriendshipStatus.Friends) == null){
            throw RuntimeException("These two users are not friends")
        }


        //Check that the conversation doesn't already exist
        val conversationAlreadyExists = findAPrivateConversationForTwoUsers(userSelf.id, userOther.id)

        //If it does, return this conversation
        if (conversationAlreadyExists != null) {
            return conversationAlreadyExists
        }

        else{
            //Create the conversation - empty values, making it obvious this is not a group conversation
            val conversationNew = conversationRepository.save(Conversation(groupConversation = false))

            val userConversationUserSelf = UserConversation()
            userConversationUserSelf.user = userSelf
            userConversationUserSelf.conversation = conversationNew
            conversationNew.userConversation.add(userConversationUserSelf)
            userConversationRepository.save(userConversationUserSelf)

            val userConversationOther = UserConversation()
            userConversationOther.user = userOther
            userConversationOther.conversation = conversationNew
            conversationNew.userConversation.add(userConversationOther)
            userConversationRepository.save(userConversationOther)

            conversationRepository.save(conversationNew)

            return conversationNew
        }
    }

    //Todo - delete saved images too
    fun deleteAllDataToDoWithConversation(conversation: Conversation){
        userConversationRepository.deleteAllByConversationId(conversation.id)
        messageRepository.deleteAllByConversationId(conversation.id)
        conversationRepository.delete(conversation)
    }
}
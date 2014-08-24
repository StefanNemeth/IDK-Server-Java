/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.achievement.readers.GetAchievementListReader;
import org.stevewinfield.suja.idk.communication.catalog.readers.*;
import org.stevewinfield.suja.idk.communication.catalog.recycler.readers.GetRecyclerConfigurationReader;
import org.stevewinfield.suja.idk.communication.catalog.recycler.readers.GetRewardsListReader;
import org.stevewinfield.suja.idk.communication.catalog.recycler.readers.RecycleItemsReader;
import org.stevewinfield.suja.idk.communication.friendstream.readers.FriendStreamEventRequestReader;
import org.stevewinfield.suja.idk.communication.friendstream.readers.ToggleFriendStreamReader;
import org.stevewinfield.suja.idk.communication.global.readers.DebugEventReader;
import org.stevewinfield.suja.idk.communication.global.readers.LatencyTestReader;
import org.stevewinfield.suja.idk.communication.handshake.readers.AuthenticatePlayerReader;
import org.stevewinfield.suja.idk.communication.handshake.readers.InitializeCryptoReader;
import org.stevewinfield.suja.idk.communication.handshake.readers.ShowDayMessageReader;
import org.stevewinfield.suja.idk.communication.inventory.readers.GetObjectInventoryReader;
import org.stevewinfield.suja.idk.communication.messenger.readers.*;
import org.stevewinfield.suja.idk.communication.moderation.readers.GetModerationPlayerInfoReader;
import org.stevewinfield.suja.idk.communication.moderation.readers.GetModerationRoomInfoReader;
import org.stevewinfield.suja.idk.communication.moderation.readers.ModerationPlayerMessageReader;
import org.stevewinfield.suja.idk.communication.navigator.readers.*;
import org.stevewinfield.suja.idk.communication.player.readers.*;
import org.stevewinfield.suja.idk.communication.quests.readers.GetQuestsListReader;
import org.stevewinfield.suja.idk.communication.room.readers.*;
import org.stevewinfield.suja.idk.communication.room.settings.readers.EditRoomReader;
import org.stevewinfield.suja.idk.communication.room.settings.readers.RoomEditGetInfoReader;
import org.stevewinfield.suja.idk.communication.room.wired.readers.SaveWiredReader;
import org.stevewinfield.suja.idk.communication.trading.readers.*;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.util.HashMap;
import java.util.Map;

public class MessageHandler {
    private static Logger logger = Logger.getLogger(MessageHandler.class);
    private static Map<Short, IMessageReader> messages;

    public static void loadMessages() {
        messages = new HashMap<Short, IMessageReader>();
        putHandshakeMessages();
        putPlayerMessages();
        putGlobalMessages();
        putAchievementMessages();
        putMessengerMessages();
        putNavigatorMessages();
        putRoomMessages();
        putInventoryMessages();
        putCatalogMessages();
        putRecyclerMessages();
        putFriendStreamMessages();
        putModerationMessages();
        putQuestsMessages();
        logger.info(messages.size() + " Message Reader(s) loaded.");
    }

    private static void putHandshakeMessages() {
        messages.put(OperationCodes.getIncomingOpCode("InitCrypto"), new InitializeCryptoReader());
        messages.put(OperationCodes.getIncomingOpCode("AuthenticatePlayer"), new AuthenticatePlayerReader());
    }

    private static void putPlayerMessages() {
        messages.put(OperationCodes.getIncomingOpCode("GetPlayerInfo"), new GetPlayerInfoReader());
        messages.put(OperationCodes.getIncomingOpCode("PlayerBalance"), new GetPlayerBalanceReader());
        messages.put(OperationCodes.getIncomingOpCode("DayMessage"), new ShowDayMessageReader());
        messages.put(OperationCodes.getIncomingOpCode("SetPlayerFigure"), new SetPlayerFigureReader());
        messages.put(OperationCodes.getIncomingOpCode("GetSubscriptionData"), new GetSubscriptionDataReader());
        messages.put(OperationCodes.getIncomingOpCode("SetPlayerMotto"), new SetPlayerMottoReader());
    }

    private static void putGlobalMessages() {
        messages.put(OperationCodes.getIncomingOpCode("DebugEvent"), new DebugEventReader());
        messages.put(OperationCodes.getIncomingOpCode("LatencyTest"), new LatencyTestReader());
    }

    private static void putAchievementMessages() {
        messages.put(OperationCodes.getIncomingOpCode("GetAchievementList"), new GetAchievementListReader());
    }

    private static void putMessengerMessages() {
        messages.put(OperationCodes.getIncomingOpCode("InitializeMessenger"), new InitializeMessengerReader());
        messages.put(OperationCodes.getIncomingOpCode("SendInstantMessage"), new SendInstantMessageReader());
        messages.put(OperationCodes.getIncomingOpCode("FriendRequestAccept"), new FriendRequestAcceptReader());
        messages.put(OperationCodes.getIncomingOpCode("FriendRequestDecline"), new FriendRequestDeclineReader());
        messages.put(OperationCodes.getIncomingOpCode("MessengerSearch"), new MessengerSearchReader());
        messages.put(OperationCodes.getIncomingOpCode("FriendRemove"), new FriendRemoveReader());
        messages.put(OperationCodes.getIncomingOpCode("SendFriendRequest"), new SendFriendRequestReader());
        messages.put(OperationCodes.getIncomingOpCode("SendInstantInvite"), new SendInstantInviteReader());
        messages.put(OperationCodes.getIncomingOpCode("OnFollowBuddy"), new OnFollowBuddyReader());
    }

    private static void putNavigatorMessages() {
        messages.put(OperationCodes.getIncomingOpCode("ListPopularRooms"), new ListPopularRoomsReader());
        messages.put(OperationCodes.getIncomingOpCode("ListPlayerRooms"), new ListPlayerRoomsReader());
        messages.put(OperationCodes.getIncomingOpCode("CheckCanCreateRoom"), new CheckCanCreateRoomReader());
        messages.put(OperationCodes.getIncomingOpCode("CreateRoom"), new CreateRoomReader());
        messages.put(OperationCodes.getIncomingOpCode("GetRoomCategories"), new GetRoomCategoriesReader());
        messages.put(OperationCodes.getIncomingOpCode("GetOfficialRooms"), new GetOfficialRoomsReader());
        messages.put(OperationCodes.getIncomingOpCode("GetPopularTags"), new GetPopularTagsReader());
        messages.put(OperationCodes.getIncomingOpCode("PerformNavigatorSearch"), new PerformNavigatorSearchReader());
        messages.put(OperationCodes.getIncomingOpCode("PerformNavigatorTagSearch"), new PerformNavigatorSearchReader());
        messages.put(OperationCodes.getIncomingOpCode("AddFavorite"), new AddFavoriteReader());
        messages.put(OperationCodes.getIncomingOpCode("RemoveFavorite"), new RemoveFavoriteReader());
    }

    private static void putRoomMessages() {
        messages.put(OperationCodes.getIncomingOpCode("OpenFlatConnection"), new OpenFlatConnectionReader());
        messages.put(OperationCodes.getIncomingOpCode("GetGroupBadges"), new GetGroupBadgesReader());
        messages.put(OperationCodes.getIncomingOpCode("GetFurniCampaigns"), new GetFurniCampaignsReader());
        messages.put(OperationCodes.getIncomingOpCode("GetRoomModel"), new GetRoomModelReader());
        messages.put(OperationCodes.getIncomingOpCode("GetRoomObjects"), new GetRoomObjectsReader());
        messages.put(OperationCodes.getIncomingOpCode("RoomPlayerChatShout"), new RoomPlayerChatReader());
        messages.put(OperationCodes.getIncomingOpCode("RoomPlayerChatTalk"), new RoomPlayerChatReader());
        messages.put(OperationCodes.getIncomingOpCode("RoomPlayerStartTyping"), new TypingStateChangedReader());
        messages.put(OperationCodes.getIncomingOpCode("RoomPlayerStopTyping"), new TypingStateChangedReader());
        messages.put(OperationCodes.getIncomingOpCode("MoveFloorItem"), new MoveFloorItemReader());
        messages.put(OperationCodes.getIncomingOpCode("PlayerMove"), new PlayerMoveReader());
        messages.put(OperationCodes.getIncomingOpCode("TriggerRoomFloorItem"), new TriggerRoomItemReader());
        messages.put(OperationCodes.getIncomingOpCode("TriggerRoomWallItem"), new TriggerRoomItemReader());
        messages.put(OperationCodes.getIncomingOpCode("PlaceItem"), new PlaceItemReader());
        messages.put(OperationCodes.getIncomingOpCode("RoomItemPlaceSticky"), new PlaceItemReader());
        messages.put(OperationCodes.getIncomingOpCode("TakeItem"), new TakeItemReader());
        messages.put(OperationCodes.getIncomingOpCode("RoomPlayerWaves"), new RoomPlayerWavesReader());
        messages.put(OperationCodes.getIncomingOpCode("RoomPlayerDance"), new RoomPlayerDanceReader());
        messages.put(OperationCodes.getIncomingOpCode("RoomInterstitial"), new RoomInterstitialReader());
        messages.put(OperationCodes.getIncomingOpCode("SaveWiredEffect"), new SaveWiredReader());
        messages.put(OperationCodes.getIncomingOpCode("SaveWiredTrigger"), new SaveWiredReader());
        messages.put(OperationCodes.getIncomingOpCode("RoomEditGetInfo"), new RoomEditGetInfoReader());
        messages.put(OperationCodes.getIncomingOpCode("EditRoom"), new EditRoomReader());
        messages.put(OperationCodes.getIncomingOpCode("RoomKick"), new RoomKickReader());
        messages.put(OperationCodes.getIncomingOpCode("MoveWallItem"), new MoveWallItemReader());
        messages.put(OperationCodes.getIncomingOpCode("RoomApplyDecoration"), new RoomApplyDecorationReader());
        messages.put(OperationCodes.getIncomingOpCode("GetMoodlightInfo"), new GetMoodlightInfoReader());
        messages.put(OperationCodes.getIncomingOpCode("SwitchMoodlight"), new SwitchMoodlightReader());
        messages.put(OperationCodes.getIncomingOpCode("UpdateMoodlight"), new UpdateMoodlightReader());
        messages.put(OperationCodes.getIncomingOpCode("ItemActivateWheel"), new TriggerRoomItemReader());
        messages.put(OperationCodes.getIncomingOpCode("ItemActivateExchange"), new ItemActivateExchangeReader());
        messages.put(OperationCodes.getIncomingOpCode("ItemActivateDice"), new TriggerRoomItemReader());
        messages.put(OperationCodes.getIncomingOpCode("ItemClearDice"), new TriggerRoomItemReader());
        messages.put(OperationCodes.getIncomingOpCode("InitiateTrade"), new InitiateTradeReader());
        messages.put(OperationCodes.getIncomingOpCode("TradeOffer"), new TradeOfferReader());
        messages.put(OperationCodes.getIncomingOpCode("TradeTakeBack"), new TradeTakeBackReader());
        messages.put(OperationCodes.getIncomingOpCode("TradeAccept"), new TradeAcceptReader());
        messages.put(OperationCodes.getIncomingOpCode("TradeComplete"), new TradeCompleteReader());
        messages.put(OperationCodes.getIncomingOpCode("TradeModify"), new TradeModifyReader());
        messages.put(OperationCodes.getIncomingOpCode("TradeStop1"), new TradeStopReader());
        messages.put(OperationCodes.getIncomingOpCode("TradeStop2"), new TradeStopReader());
        messages.put(OperationCodes.getIncomingOpCode("PlayerChangeRotation"), new PlayerChangeRotationReader());
        messages.put(OperationCodes.getIncomingOpCode("PlayerWhisper"), new PlayerWhisperReader());
        messages.put(OperationCodes.getIncomingOpCode("AnswerDoorbell"), new AnswerDoorbellReader());
        messages.put(OperationCodes.getIncomingOpCode("ContinueLoadingAfterDoorbell"), new ContinueLoadingAfterDoorbellReader());
        messages.put(OperationCodes.getIncomingOpCode("RateRoom"), new RateRoomReader());
        messages.put(OperationCodes.getIncomingOpCode("SetHomeRoom"), new SetHomeRoomReader());
        messages.put(OperationCodes.getIncomingOpCode("GetRoomInformation"), new GetRoomInformationReader());
        messages.put(OperationCodes.getIncomingOpCode("RespectPlayer"), new RespectPlayerReader());
        messages.put(OperationCodes.getIncomingOpCode("GiveRights"), new GiveRightsReader());
        messages.put(OperationCodes.getIncomingOpCode("TakeRights"), new TakeRightsReader());
        messages.put(OperationCodes.getIncomingOpCode("TakeAllRights"), new TakeAllRightsReader());
        messages.put(OperationCodes.getIncomingOpCode("OpenSticky"), new OpenStickyReader());
        messages.put(OperationCodes.getIncomingOpCode("SaveSticky"), new SaveStickyReader());
        messages.put(OperationCodes.getIncomingOpCode("DeleteSticky"), new DeleteStickyReader());
        messages.put(OperationCodes.getIncomingOpCode("OpenBox"), new OpenBoxReader());
    }

    private static void putInventoryMessages() {
        messages.put(OperationCodes.getIncomingOpCode("GetObjectInventory"), new GetObjectInventoryReader());
    }

    private static void putCatalogMessages() {
        messages.put(OperationCodes.getIncomingOpCode("GetCatalogIndex"), new GetCatalogIndexReader());
        messages.put(OperationCodes.getIncomingOpCode("GetCatalogPage"), new GetCatalogPageReader());
        messages.put(OperationCodes.getIncomingOpCode("PurchaseItem"), new PurchaseItemReader());
        messages.put(OperationCodes.getIncomingOpCode("GetClubOffers"), new GetClubOffersReader());
        messages.put(OperationCodes.getIncomingOpCode("GetMonthlyClubGifts"), new GetMonthlyClubGiftsReader());
        messages.put(OperationCodes.getIncomingOpCode("ChooseCatalogClubGift"), new ChooseCatalogClubGiftReader());
        messages.put(OperationCodes.getIncomingOpCode("CheckCanGift"), new CheckCanGiftReader());
        messages.put(OperationCodes.getIncomingOpCode("GetCatalogGiftWrappingSettings"), new GetCatalogGiftWrappingSettingsReader());
        messages.put(OperationCodes.getIncomingOpCode("PurchaseGiftItem"), new PurchaseGiftItemReader());
    }

    private static void putRecyclerMessages() {
        messages.put(OperationCodes.getIncomingOpCode("GetRewardsList"), new GetRewardsListReader());
        messages.put(OperationCodes.getIncomingOpCode("GetRecyclerConfiguration"), new GetRecyclerConfigurationReader());
        messages.put(OperationCodes.getIncomingOpCode("RecycleItems"), new RecycleItemsReader());
    }

    private static void putFriendStreamMessages() {
        messages.put(OperationCodes.getIncomingOpCode("ToggleFriendStream"), new ToggleFriendStreamReader());
        messages.put(OperationCodes.getIncomingOpCode("FriendStreamEventRequest"), new FriendStreamEventRequestReader());
    }

    private static void putModerationMessages() {
        messages.put(OperationCodes.getIncomingOpCode("GetModerationRoomInfo"), new GetModerationRoomInfoReader());
        messages.put(OperationCodes.getIncomingOpCode("GetModerationPlayerInfo"), new GetModerationPlayerInfoReader());
        messages.put(OperationCodes.getIncomingOpCode("ModerationPlayerMessage"), new ModerationPlayerMessageReader());
    }

    private static void putQuestsMessages() {
        messages.put(OperationCodes.getIncomingOpCode("GetQuestsList"), new GetQuestsListReader());
    }

    public static void handleMessage(final Session session, final MessageReader reader) {
        final boolean contains = messages.containsKey(reader.getMessageId());
        if (IDK.DEBUG) {
            logger.debug("REC #" + reader.getMessageId() + " " + (contains ? "[HANDLING]" : "") + " " + reader.getDebugString());
        }
        if (messages.containsKey(reader.getMessageId())) {
            messages.get(reader.getMessageId()).parse(session, reader);
        }
        reader.dispose();
    }
}

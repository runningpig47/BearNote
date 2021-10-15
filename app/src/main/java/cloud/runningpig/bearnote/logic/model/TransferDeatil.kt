package cloud.runningpig.bearnote.logic.model

import androidx.room.DatabaseView
import java.util.*

// TODO '' 是否可以为NULL
@DatabaseView(
    viewName = "transfer_detail", value =
    // UNION 合并两张表：note_detail & transfer
    "SELECT noteId, " +
            "categoryIcon, " +
            "categoryName, " +
            "categorySort, " +
            "noteAmount, " +
            "accountId, " +
            "accountName, " +
            "noteDate, " +
            "information, " +
            "0 as detailSort, " +
            "'' as fromId, " +
            "'' as toId, " +
            "'' as fromName, " +
            "'' as toName " +
            "FROM note_detail WHERE accountId != -1 UNION " +

            "SELECT transfer.id, " +
            "'', " +
            "'', " +
            "'', " +
            "transfer.amount, " +
            "'', " +
            "'', " +
            "transfer.date, " +
            "transfer.information, " +
            "1 as detailSort, " +
            "transfer.from_id, " +
            "transfer.to_id, " +
            "a1.name, " +
            "a2.name " +
            "FROM transfer INNER JOIN account a1, account a2 ON from_id = a1.id AND to_id = a2.id " +
            "ORDER BY noteDate"
)
data class TransferDetail(
    var noteId: Int, // 记账id & 转账id
    var categoryIcon: String, // 类别图标 & ''
    var categoryName: String, // 类别名称 & ''
    var categorySort: Int, // 类别种类 & ''
    var noteAmount: Double, // 记账金额 & 转账金额
    var accountId: Int, // 记账账户id & ''
    var accountName: String, // 记账账户名称 & ''
    var noteDate: Date, // 记账日期 & 转账日期
    var information: String, // 记账备注 & 转账备注（注意：资金调整会用这个字段记录调整金额，会转为double使用）
    var detailSort: Int,
    var fromId: Int,
    var toId: Int,
    var fromName: String,
    var toName: String
)
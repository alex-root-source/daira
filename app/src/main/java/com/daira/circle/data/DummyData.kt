package com.daira.circle.data

data class Post(
    val name: String,
    val initials: String,
    val time: String,
    val text: String,
    val hasImage: Boolean,
    val likes: Int,
    val comments: Int
)

// ملاحظة: المنشورات لا تزال وهمية مؤقتًا (مؤجلة حسب الاتفاق) —
// أما الأعضاء ورمز الدعوة والدردشة فأصبحت الآن تُقرأ من قاعدة بيانات حقيقية (Room)
// راجع: data/db/DairaDatabase.kt
object DummyData {
    val posts = listOf(
        Post("ريم القاسم", "ريم", "منذ ٢٠ دقيقة", "رحلة اليوم كانت أجمل من المتوقع 🌿 وحشتوني يا جماعة", true, 5, 2),
        Post("سعد المطيري", "سعد", "منذ ساعتين", "من قال إن السهرة لازم لها مناسبة؟ الليلة عندي 😄", false, 9, 4),
        Post("لينا حداد", "لينا", "أمس", "كيكة اليوم طلعت أحسن من المرة اللي فاتت 🍰", true, 7, 1),
    )
}

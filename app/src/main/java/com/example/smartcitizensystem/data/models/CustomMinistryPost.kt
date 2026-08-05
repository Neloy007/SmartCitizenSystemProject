package com.example.smartcitizensystem.data.models

data class CustomMinistryPost(
    val id: String,
    val ministryName: String,
    val ministryKey: String,
    val title: String,
    val body: String,
    val link: String,
    val avatar: String,
    val date: String,
    val likes: Int = 0,
    val shares: Int = 0
)

object CustomMinistryPostsData {
    fun getPosts(): List<CustomMinistryPost> = listOf(
        CustomMinistryPost(
            id = "1",
            ministryName = "Ministry of Education",
            ministryKey = "moedu",
            title = "📚 নতুন শিক্ষাক্রম বাস্তবায়নের নির্দেশনা ২০২৬",
            body = "মাধ্যমিক ও উচ্চ শিক্ষা বিভাগ কর্তৃক নতুন শিক্ষাক্রম বাস্তবায়নের জন্য সকল শিক্ষাপ্রতিষ্ঠানকে নির্দেশনা প্রদান করা হয়েছে। আগামী ২০২৭ শিক্ষাবর্ষ থেকে নতুন শিক্ষাক্রম কার্যকর হবে।",
            link = "https://moedu.gov.bd",
            avatar = "🏢",
            date = "২ ঘণ্টা আগে",
            likes = 45,
            shares = 12
        ),
        CustomMinistryPost(
            id = "2",
            ministryName = "ICT Division",
            ministryKey = "ict",
            title = "💻 ডিজিটাল বাংলাদেশ: স্মার্ট সার্ভিস ডেলিভারি প্ল্যাটফর্ম চালু",
            body = "তথ্য ও যোগাযোগ প্রযুক্তি বিভাগ কর্তৃক নাগরিকদের জন্য স্মার্ট সার্ভিস ডেলিভারি প্ল্যাটফর্ম চালু করা হয়েছে। এই প্ল্যাটফর্মের মাধ্যমে ৫০টির বেশি সেবা অনলাইনে গ্রহণ করা যাবে।",
            link = "https://ictd.gov.bd",
            avatar = "💻",
            date = "৪ ঘণ্টা আগে",
            likes = 67,
            shares = 23
        ),
        CustomMinistryPost(
            id = "3",
            ministryName = "Ministry of Health & Family Welfare",
            ministryKey = "mohfw",
            title = "🏥 স্বাস্থ্যসেবা ডিজিটালাইজেশন প্রকল্পের অগ্রগতি",
            body = "স্বাস্থ্য ও পরিবার কল্যাণ মন্ত্রণালয়ের ডিজিটালাইজেশন প্রকল্পের অগ্রগতি নিয়ে বিশেষ সভা অনুষ্ঠিত হয়েছে। ২০২৬ সালের মধ্যে সকল স্বাস্থ্য কেন্দ্র ডিজিটাল সেবার আওতায় আসবে।",
            link = "https://mohfw.gov.bd",
            avatar = "🏥",
            date = "৬ ঘণ্টা আগে",
            likes = 89,
            shares = 34
        ),
        CustomMinistryPost(
            id = "4",
            ministryName = "Ministry of Primary & Mass Education",
            ministryKey = "mopme",
            title = "🎒 প্রাথমিক শিক্ষায় আইসিটি অন্তর্ভুক্তির পরিকল্পনা",
            body = "প্রাথমিক ও গণশিক্ষা মন্ত্রণালয় প্রাথমিক শিক্ষায় আইসিটি অন্তর্ভুক্তির জন্য নতুন পরিকল্পনা গ্রহণ করেছে। ২০২৭ সালের মধ্যে সকল প্রাথমিক বিদ্যালয়ে কম্পিউটার ল্যাব স্থাপন করা হবে।",
            link = "https://mopme.gov.bd",
            avatar = "🎒",
            date = "৮ ঘণ্টা আগে",
            likes = 53,
            shares = 18
        ),
        CustomMinistryPost(
            id = "5",
            ministryName = "Ministry of Education",
            ministryKey = "moedu",
            title = "📖 উচ্চ শিক্ষায় গবেষণা বাজেট বৃদ্ধি",
            body = "উচ্চ শিক্ষায় গবেষণা ও উদ্ভাবনের জন্য বাজেট বৃদ্ধি করা হয়েছে। গবেষণায় উৎসাহিত করতে নতুন ফেলোশিপ এবং গ্রান্ট প্রোগ্রাম চালু করা হবে।",
            link = "https://moedu.gov.bd",
            avatar = "🏢",
            date = "১০ ঘণ্টা আগে",
            likes = 34,
            shares = 9
        ),
        CustomMinistryPost(
            id = "6",
            ministryName = "ICT Division",
            ministryKey = "ict",
            title = "🌐 সাইবার নিরাপত্তা সচেতনতা কর্মসূচি",
            body = "ডিজিটাল নিরাপত্তা নিশ্চিত করতে সাইবার নিরাপত্তা সচেতনতা কর্মসূচি শুরু করেছে আইসিটি বিভাগ। নাগরিকদের সাইবার হুমকি সম্পর্কে সচেতন করতে এই কর্মসূচি পরিচালিত হবে।",
            link = "https://ictd.gov.bd",
            avatar = "💻",
            date = "১২ ঘণ্টা আগে",
            likes = 78,
            shares = 42
        )
    )
}
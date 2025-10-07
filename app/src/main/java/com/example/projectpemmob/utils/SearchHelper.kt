package com.example.projectpemmob.utils

object SearchHelper {
    
    /**
     * Generate search keywords for better matching
     */
    fun generateSearchKeywords(text: String): List<String> {
        val keywords = mutableListOf<String>()
        
        // Add original text
        keywords.add(text.lowercase())
        
        // Add individual words
        text.split(" ").forEach { word ->
            if (word.isNotBlank()) {
                keywords.add(word.lowercase())
            }
        }
        
        // Add common variations for popular places
        when (text.lowercase()) {
            "dieng plateau", "dieng" -> {
                keywords.addAll(listOf("dieng", "plateau", "dataran tinggi"))
            }
            "telaga warna", "telaga" -> {
                keywords.addAll(listOf("telaga", "warna", "danau"))
            }
            "bukit sikunir" -> {
                keywords.addAll(listOf("bukit", "sikunir", "sunrise"))
            }
            "mie ongklok" -> {
                keywords.addAll(listOf("mie", "ongklok", "mi", "makanan khas"))
            }
        }
        
        return keywords.distinct()
    }
    
    /**
     * Calculate search relevance score with enhanced logic for progressive search
     */
    fun calculateRelevanceScore(query: String, targetText: String, category: String = "", location: String = ""): Int {
        var score = 0
        val queryLower = query.lowercase().trim()
        val targetLower = targetText.lowercase()
        val categoryLower = category.lowercase()
        val locationLower = location.lowercase()
        
        // Early return for empty query
        if (queryLower.isEmpty()) return 0
        
        // Exact match gets highest score
        if (targetLower == queryLower) score += 1000
        
        // Starts with query gets very high score
        if (targetLower.startsWith(queryLower)) score += 500
        
        // Word starts with query gets high score
        targetLower.split(" ").forEach { word ->
            if (word.startsWith(queryLower)) score += 300
        }
        
        // Category/location starts with query
        if (categoryLower.startsWith(queryLower)) score += 200
        if (locationLower.startsWith(queryLower)) score += 150
        
        // Contains query gets medium score
        if (targetLower.contains(queryLower)) score += 100
        if (categoryLower.contains(queryLower)) score += 50
        if (locationLower.contains(queryLower)) score += 30
        
        // Bonus for shorter names (more specific matches)
        if (targetText.length <= 20) score += 25
        
        // Progressive matching - each character match from start
        for (i in queryLower.indices) {
            if (i < targetLower.length && targetLower[i] == queryLower[i]) {
                score += 10
            }
        }
        
        return score
    }
    
    /**
     * Enhanced search suggestions based on input length
     */
    fun getSmartSuggestions(currentQuery: String): List<String> {
        val queryLower = currentQuery.lowercase().trim()
        
        return when {
            queryLower.isEmpty() -> getPopularSearches().take(5)
            queryLower.length == 1 -> {
                getPopularSearches().filter { it.lowercase().startsWith(queryLower) }
            }
            else -> {
                getPopularSearches().filter { 
                    it.lowercase().contains(queryLower) 
                }.sortedBy { 
                    if (it.lowercase().startsWith(queryLower)) 0 else 1 
                }
            }
        }
    }
    
    /**
     * Enhanced popular search suggestions
     */
    fun getPopularSearches(): List<String> {
        return listOf(
            "Dieng Plateau",
            "Bukit Sikunir", 
            "Telaga Warna",
            "Mie Ongklok",
            "Dawet Ayu",
            "Bukit",
            "Telaga",
            "Curug",
            "Agrowisata",
            "Kuliner Wonosobo",
            "Wisata Alam",
            "Sunrise Point",
            "Danau",
            "Makanan Khas",
            "Tempat Wisata"
        )
    }
}
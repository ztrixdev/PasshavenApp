//
//  Item.swift
//  PasshavenApp
//
//  Created by Wladyslaw Skiba on 18/9/2025.
//

import Foundation
import SwiftData

@Model
final class Item {
    var timestamp: Date
    
    init(timestamp: Date) {
        self.timestamp = timestamp
    }
}
